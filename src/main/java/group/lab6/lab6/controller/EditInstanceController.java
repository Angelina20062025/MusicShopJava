package group.lab6.lab6.controller;

import group.lab6.lab6.model.*;
import group.lab6.lab6.service.VinylService;
import group.lab6.lab6.service.exceptions.ValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class EditInstanceController {

    @FXML private Label instanceInfoLabel;
    @FXML private TextField priceField;
    @FXML private TextField shelfField;
    @FXML private TextField sectionField;
    @FXML private TextField boxField;
    @FXML private ComboBox<FormatType> formatComboBox;
    @FXML private ComboBox<SpeedType> speedComboBox;
    @FXML private Label statusLabel;

    private VinylService vinylService;
    private Instance currentInstance;

    public void setVinylService(VinylService vinylService) {
        this.vinylService = vinylService;
    }

    public void setInstance(Instance instance) {
        this.currentInstance = instance;
        if (instance != null) {
            instanceInfoLabel.setText(String.format("%s - %s",
                    instance.getRelease().getArtist(),
                    instance.getRelease().getAlbumTitle()));

            priceField.setText(String.format("%.2f", instance.getPrice()));
            shelfField.setText(instance.getLocationShelf());
            sectionField.setText(instance.getLocationSection());
            boxField.setText(instance.getLocationBox());
            formatComboBox.setValue(instance.getFormat());
            speedComboBox.setValue(instance.getSpeed());
        }
    }

    @FXML
    public void initialize() {
        formatComboBox.getItems().setAll(FormatType.values());
        speedComboBox.getItems().setAll(SpeedType.values());
    }

    @FXML
    private void handleSave() {
        try {
            BigDecimal price = getPrice();
            String shelf = shelfField.getText().trim();
            String section = sectionField.getText().trim();
            String box = boxField.getText().trim();
            FormatType format = formatComboBox.getValue();
            SpeedType speed = speedComboBox.getValue();

            if (format == null) {
                throw new ValidationException("Формат не выбран");
            }
            if (speed == null) {
                throw new ValidationException("Скорость не выбрана");
            }

            currentInstance.setPrice(price);
            currentInstance.setLocationShelf(shelf.isEmpty() ? null : shelf);
            currentInstance.setLocationSection(section.isEmpty() ? null : section);
            currentInstance.setLocationBox(box.isEmpty() ? null : box);
            currentInstance.setFormat(format);
            currentInstance.setSpeed(speed);

            vinylService.updateInstance(currentInstance);

            statusLabel.setText("Сохранено успешно");
            Stage stage = (Stage) priceField.getScene().getWindow();
            stage.close();

        } catch (ValidationException e) {
            showAlert("Ошибка", e.getMessage());
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось сохранить: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) priceField.getScene().getWindow();
        stage.close();
    }

    private BigDecimal getPrice() {
        String priceText = priceField.getText().trim();
        if (priceText.isEmpty()) {
            throw new ValidationException("Цена не может быть пустой");
        }
        try {
            priceText = priceText.replace(',', '.');
            BigDecimal price = new BigDecimal(priceText);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException("Цена должна быть больше 0");
            }
            return price;
        } catch (NumberFormatException e) {
            throw new ValidationException("Некорректный формат цены");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}