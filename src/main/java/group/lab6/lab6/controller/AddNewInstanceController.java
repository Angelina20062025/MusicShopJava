package group.lab6.lab6.controller;

import group.lab6.lab6.model.FormatType;
import group.lab6.lab6.model.Genre;
import group.lab6.lab6.model.SpeedType;
import group.lab6.lab6.model.Supplier;
import group.lab6.lab6.model.Release;
import group.lab6.lab6.service.CatalogService;
import group.lab6.lab6.service.ReferenceService;
import group.lab6.lab6.service.VinylService;
import group.lab6.lab6.service.exceptions.ValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class AddNewInstanceController {

    @FXML
    private TextField catalogNumberField;

    @FXML
    private TextField artistField;

    @FXML
    private TextField albumField;

    @FXML
    private ComboBox<Genre> genreComboBox;

    @FXML
    private TextField labelField;

    @FXML
    private TextField countryField;

    @FXML
    private TextField yearField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField priceField;

    @FXML
    private ComboBox<FormatType> formatComboBox;

    @FXML
    private ComboBox<SpeedType> speedComboBox;

    @FXML
    private ComboBox<Supplier> supplierComboBox;

    @FXML
    private TextField shelfField;

    @FXML
    private TextField sectionField;

    @FXML
    private TextField boxField;

    @FXML
    private Label statusLabel;

    private VinylService vinylService;
    private CatalogService catalogService;
    private ReferenceService referenceService;

    private Integer existingReleaseId; // ID существующего релиза (если найден)
    private boolean isNewRelease = true; // флаг: новый релиз или существующий

    public void setVinylService(VinylService vinylService) {
        this.vinylService = vinylService;
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
        loadGenres();
    }

    public void setReferenceService(ReferenceService referenceService) {
        this.referenceService = referenceService;
        loadSuppliers();
    }

    @FXML
    public void initialize() {
        formatComboBox.getItems().setAll(FormatType.values());
        speedComboBox.getItems().setAll(SpeedType.values());

        catalogNumberField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                checkExistingRelease();
            }
        });
    }

    private void checkExistingRelease() {
        String catalogNumber = catalogNumberField.getText().trim();
        if (catalogNumber.isEmpty()) {
            return;
        }

        try {
            Optional<Release> existingRelease = catalogService.findByCatalogNumber(catalogNumber);

            if (existingRelease.isPresent()) {
                Release release = existingRelease.get();
                existingReleaseId = release.getReleaseId().intValue();
                isNewRelease = false;

                artistField.setText(release.getArtist());
                albumField.setText(release.getAlbumTitle());
                genreComboBox.setValue(release.getGenre());
                labelField.setText(release.getLabel());
                countryField.setText(release.getCountry());
                if (release.getReleaseYear() != null) {
                    yearField.setText(String.valueOf(release.getReleaseYear()));
                }
                descriptionField.setText(release.getDescription());

                setReleaseFieldsEditable(false);

                statusLabel.setText("Релиз найден.");
            } else {
                existingReleaseId = null;
                isNewRelease = true;

                clearReleaseFields();
                setReleaseFieldsEditable(true);

                statusLabel.setText("Новый релиз. Заполните информацию о пластинке.");
            }
        } catch (Exception e) {
            statusLabel.setText("Ошибка проверки: " + e.getMessage());
        }
    }

    private void setReleaseFieldsEditable(boolean editable) {
        artistField.setEditable(editable);
        albumField.setEditable(editable);
//        genreComboBox.setEditable(editable);
        labelField.setEditable(editable);
        countryField.setEditable(editable);
        yearField.setEditable(editable);
        descriptionField.setEditable(editable);
    }

    private void clearReleaseFields() {
        artistField.clear();
        albumField.clear();
        genreComboBox.setValue(null);
        labelField.clear();
        countryField.clear();
        yearField.clear();
        descriptionField.clear();
        existingReleaseId = null;
    }

    private void loadGenres() {
        if (catalogService != null) {
            genreComboBox.getItems().clear();
            genreComboBox.getItems().addAll(catalogService.getAllGenres());
        }
    }

    private void loadSuppliers() {
        if (referenceService != null) {
            supplierComboBox.getItems().clear();
            supplierComboBox.getItems().addAll(referenceService.getAllSuppliers());
        }
    }

    @FXML
    private void handleAdd() {
        try {
            String catalogNumber = getRequiredText(catalogNumberField, "Каталоговый номер");
            BigDecimal price = getRequiredPrice(priceField);
            FormatType format = getRequiredFormat();
            SpeedType speed = getRequiredSpeed();
            Integer supplierId = getRequiredSupplier();

            String locationShelf = shelfField.getText().trim();
            String locationSection = sectionField.getText().trim();
            String locationBox = boxField.getText().trim();

            Integer instanceId;

            if (isNewRelease) {
                String artist = getRequiredText(artistField, "Исполнитель");
                String album = getRequiredText(albumField, "Название альбома");
                Integer genreId = null;
                if (genreComboBox.getValue() != null) {
                    genreId = genreComboBox.getValue().getGenreId().intValue();
                }
                String label = labelField.getText().trim();
                String country = countryField.getText().trim();
                validateCountry(country);
                Integer year = null;
                if (!yearField.getText().trim().isEmpty()) {
                    try {
                        year = Integer.parseInt(yearField.getText().trim());
                    } catch (NumberFormatException e) {
                        throw new ValidationException("Некорректный формат года");
                    }
                }
                String description = descriptionField.getText().trim();

                instanceId = vinylService.addNewInstanceWithRelease(
                        catalogNumber, artist, album, genreId, price,
                        format.name(), speed.toDbValue(),
                        supplierId, locationShelf, locationSection, locationBox,
                        label, country, year, description
                );
                statusLabel.setText("Успешно добавлен новый релиз и экземпляр." + instanceId);
            } else {
                // Существующий релиз — добавляем только экземпляр
                instanceId = vinylService.addNewInstanceToExistingRelease(
                        existingReleaseId, price, format.name(), speed.toDbValue(),
                        supplierId, locationShelf, locationSection, locationBox
                );
                statusLabel.setText("Успешно добавлен экземпляр к существующему релизу." + instanceId);
            }

            clearForm();

        } catch (ValidationException e) {
            showAlert("Ошибка", e.getMessage());
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось добавить пластинку: " + e.getMessage());
        }
    }

    private void validateCountry(String country) {
        if (country == null || country.trim().isEmpty()) {
            throw new ValidationException("Введите страну");
        }
        if (!country.matches("^[a-zA-Zа-яА-ЯёЁ\\s-]+$")) {
            throw new ValidationException("Страна должна содержать только буквы и пробелы");
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
        statusLabel.setText("");
        isNewRelease = true;
        existingReleaseId = null;
        setReleaseFieldsEditable(true);
        clearReleaseFields();
        catalogNumberField.clear();
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) catalogNumberField.getScene().getWindow();
        stage.close();
    }

    private void clearForm() {
        priceField.clear();
        formatComboBox.setValue(null);
        speedComboBox.setValue(null);
        supplierComboBox.setValue(null);
        shelfField.clear();
        sectionField.clear();
        boxField.clear();
        catalogNumberField.requestFocus();
    }

    private String getRequiredText(TextField field, String fieldName) {
        String value = field.getText().trim();
        if (value.isEmpty()) {
            throw new ValidationException(fieldName + " не может быть пустым");
        }
        return value;
    }

    private BigDecimal getRequiredPrice(TextField field) {
        String value = field.getText().trim();
        if (value.isEmpty()) {
            throw new ValidationException("Цена не может быть пустой");
        }
        try {
            BigDecimal price = new BigDecimal(value);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException("Цена должна быть больше 0");
            }
            return price;
        } catch (NumberFormatException e) {
            throw new ValidationException("Некорректный формат цены");
        }
    }

    private FormatType getRequiredFormat() {
        FormatType format = formatComboBox.getValue();
        if (format == null) {
            throw new ValidationException("Формат не выбран");
        }
        return format;
    }

    private SpeedType getRequiredSpeed() {
        SpeedType speed = speedComboBox.getValue();
        if (speed == null) {
            throw new ValidationException("Скорость не выбрана");
        }
        return speed;
    }

    private Integer getRequiredSupplier() {
        Supplier supplier = supplierComboBox.getValue();
        if (supplier == null) {
            throw new ValidationException("Поставщик не выбран");
        }
        return supplier.getSupplierId().intValue();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}