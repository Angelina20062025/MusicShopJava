package group.lab6.lab6.controller;

import group.lab6.lab6.model.*;
import group.lab6.lab6.service.CatalogService;
import group.lab6.lab6.service.ReferenceService;
import group.lab6.lab6.service.VinylService;
import group.lab6.lab6.service.exceptions.ValidationException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

public class AddUsedInstanceController {

    @FXML private TextField catalogNumberField;
    @FXML private TextField artistField;
    @FXML private TextField albumField;
    @FXML private ComboBox<Genre> genreComboBox;
    @FXML private TextField labelField;
    @FXML private TextField countryField;
    @FXML private TextField yearField;
    @FXML private TextField descriptionField;
    @FXML private TextField priceField;
    @FXML private ComboBox<FormatType> formatComboBox;
    @FXML private ComboBox<SpeedType> speedComboBox;
    @FXML private ComboBox<ConditionGrade> vinylConditionComboBox;
    @FXML private ComboBox<ConditionGrade> coverConditionComboBox;
    @FXML private TextField defectsNotesField;
    @FXML private TextField shelfField;
    @FXML private TextField sectionField;
    @FXML private TextField boxField;
    @FXML private ComboBox<SellerPerson> sellerComboBox;
    @FXML private Label statusLabel;

    private VinylService vinylService;
    private CatalogService catalogService;
    private ReferenceService referenceService;

    public void setVinylService(VinylService vinylService) {
        this.vinylService = vinylService;
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
        loadGenres();
    }

    public void setReferenceService(ReferenceService referenceService) {
        this.referenceService = referenceService;
        loadSellers();
    }

    @FXML
    public void initialize() {
        formatComboBox.getItems().setAll(FormatType.values());
        speedComboBox.getItems().setAll(SpeedType.values());
        vinylConditionComboBox.getItems().setAll(ConditionGrade.values());
        coverConditionComboBox.getItems().setAll(ConditionGrade.values());

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
                statusLabel.setText("Релиз найден. Добавляем Б/У экземпляр.");
            } else {
                setReleaseFieldsEditable(true);
                clearReleaseFields();
                statusLabel.setText("Новый релиз. Заполните информацию о пластинке.");
            }
        } catch (Exception e) {
            statusLabel.setText("Ошибка проверки: " + e.getMessage());
        }
    }

    private void setReleaseFieldsEditable(boolean editable) {
        artistField.setEditable(editable);
        albumField.setEditable(editable);
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
    }

    private void loadGenres() {
        if (catalogService != null) {
            genreComboBox.getItems().clear();
            genreComboBox.getItems().addAll(catalogService.getAllGenres());
        }
    }

    private void loadSellers() {
        if (referenceService != null) {
            sellerComboBox.getItems().clear();
            sellerComboBox.getItems().addAll(referenceService.getAllSellers());
        }
    }

    @FXML
    private void handleManageSellers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/group/lab6/lab6/view/SellerPersonView.fxml"));
            Parent root = loader.load();

            SellerPersonController controller = loader.getController();
            controller.setReferenceService(referenceService);

            Stage stage = new Stage();
            stage.setTitle("Управление продавцами");
            stage.setScene(new Scene(root, 800, 600));
            stage.initOwner((Stage) catalogNumberField.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();

            loadSellers();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно управления продавцами");
        }
    }

    @FXML
    private void handleAdd() {
        try {
            String catalogNumber = getRequiredText(catalogNumberField, "Каталоговый номер");
            BigDecimal price = getRequiredPrice(priceField);
            FormatType format = getRequiredFormat();
            SpeedType speed = getRequiredSpeed();
            ConditionGrade vinylCondition = getRequiredVinylCondition();
            ConditionGrade coverCondition = getRequiredCoverCondition();
            String defectsNotes = defectsNotesField.getText().trim();

            String locationShelf = shelfField.getText().trim();
            String locationSection = sectionField.getText().trim();
            String locationBox = boxField.getText().trim();

            SellerPerson selectedSeller = sellerComboBox.getValue();
            if (selectedSeller == null) {
                throw new ValidationException("Выберите продавца");
            }
//            validatePhone(selectedSeller.getPhone());
            String artist = artistField.getText().trim();
            String album = albumField.getText().trim();
            Integer genreId = null;
            if (genreComboBox.getValue() != null) {
                genreId = genreComboBox.getValue().getGenreId().intValue();
            }
//            String label = labelField.getText().trim();
            String label = labelField.getText();
            if (label == null) label = "";
            label = label.trim();
//            String country = countryField.getText().trim();
            String country = countryField.getText();
            if (country == null) country = "";
            country = country.trim();
            if (!country.isEmpty()) {
                validateCountry(country);
            }
            Integer year = null;
            if (!yearField.getText().trim().isEmpty()) {
                try {
                    year = Integer.parseInt(yearField.getText().trim());
                } catch (NumberFormatException e) {
                    throw new ValidationException("Некорректный формат года");
                }
            }
//            String description = descriptionField.getText().trim();
            String description = descriptionField.getText();
            if (description == null) description = "";
            description = description.trim();

            Integer instanceId = vinylService.addUsedInstance(
                    catalogNumber, artist, album, genreId, price,
                    format.name(), speed.toDbValue(),
                    vinylCondition.toDbValue(), coverCondition.toDbValue(), defectsNotes,
                    selectedSeller.getFirstName(), selectedSeller.getLastName(), selectedSeller.getPhone(),
                    locationShelf, locationSection, locationBox
            );

            statusLabel.setText("Успешно добавлен Б/У экземпляр!");
            clearForm();

        } catch (ValidationException e) {
            showAlert("Ошибка", e.getMessage());
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось добавить Б/У пластинку: " + e.getMessage());
        }
    }

    private void validateCountry(String country) {
        if (!country.matches("^[a-zA-Zа-яА-ЯёЁ\\s-]+$")) {
            throw new ValidationException("Страна должна содержать только буквы и пробелы");
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
        statusLabel.setText("");
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) catalogNumberField.getScene().getWindow();
        stage.close();
    }

    private void clearForm() {
        catalogNumberField.clear();
        artistField.clear();
        albumField.clear();
        genreComboBox.setValue(null);
        labelField.clear();
        countryField.clear();
        yearField.clear();
        descriptionField.clear();
        priceField.clear();
        formatComboBox.setValue(null);
        speedComboBox.setValue(null);
        vinylConditionComboBox.setValue(null);
        coverConditionComboBox.setValue(null);
        defectsNotesField.clear();
        shelfField.clear();
        sectionField.clear();
        boxField.clear();
        sellerComboBox.setValue(null);
    }

    private String getRequiredText(TextField field, String fieldName) {
        String value = field.getText().trim();
        if (value.isEmpty()) {
            throw new ValidationException(fieldName + " не может быть пустым");
        }
        return value;
    }

//    private void validatePhone(String phone) {
//        if (phone == null || phone.trim().isEmpty()) {
//            throw new ValidationException("Телефон не может быть пустым");
//        }
//        if (!phone.matches("^[\\+\\d\\s\\-\\(\\)\\/]+$")) {
//            throw new ValidationException("Телефон должен содержать только цифры и символы +, -, (, ), /");
//        }
//    }

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

    private ConditionGrade getRequiredVinylCondition() {
        ConditionGrade condition = vinylConditionComboBox.getValue();
        if (condition == null) {
            throw new ValidationException("Состояние винила не выбрано");
        }
        return condition;
    }

    private ConditionGrade getRequiredCoverCondition() {
        ConditionGrade condition = coverConditionComboBox.getValue();
        if (condition == null) {
            throw new ValidationException("Состояние обложки не выбрано");
        }
        return condition;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}