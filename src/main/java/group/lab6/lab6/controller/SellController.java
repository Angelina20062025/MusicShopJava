package group.lab6.lab6.controller;

import group.lab6.lab6.model.Instance;
import group.lab6.lab6.model.PaymentMethod;
import group.lab6.lab6.service.VinylService;
import group.lab6.lab6.service.exceptions.ValidationException;
import group.lab6.lab6.service.exceptions.InstanceNotAvailableException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class SellController {

    @FXML private Label instanceInfoLabel;
    @FXML private TextField checkNumberField;
    @FXML private ComboBox<PaymentMethod> paymentMethodComboBox;
    @FXML private TextField finalPriceField;
    @FXML private Label priceHintLabel;

    private VinylService vinylService;
    private Instance selectedInstance;
    private Stage ownerStage;

    public void setVinylService(VinylService vinylService) {
        this.vinylService = vinylService;
    }

    public void setSelectedInstance(Instance instance) {
        this.selectedInstance = instance;
        instanceInfoLabel.setText(instance.getDisplayText());
        priceHintLabel.setText("Цена экземпляра: " + instance.getPrice() + " ₽");
    }

    public void setOwnerStage(Stage ownerStage) {
        this.ownerStage = ownerStage;
    }

    @FXML
    public void initialize() {
        paymentMethodComboBox.getItems().setAll(PaymentMethod.values());
        paymentMethodComboBox.setValue(PaymentMethod.CASH);
    }

    @FXML
    private void handleSell() {
        String checkNumber = checkNumberField.getText().trim();
        PaymentMethod paymentMethod = paymentMethodComboBox.getValue();
        BigDecimal finalPrice = null;

        String priceText = finalPriceField.getText().trim();
        if (!priceText.isEmpty()) {
            try {
                finalPrice = new BigDecimal(priceText);
                if (finalPrice.compareTo(BigDecimal.ZERO) <= 0) {
                    showAlert("Ошибка", "Цена должна быть больше 0");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Некорректный формат цены");
                return;
            }
        }

        if (checkNumber.isEmpty()) {
            showAlert("Ошибка", "Номер чека не может быть пустым");
            return;
        }

        if (paymentMethod == null) {
            showAlert("Ошибка", "Выберите способ оплаты");
            return;
        }

        try {
            Integer saleId = vinylService.sellInstance(
                    selectedInstance.getInstanceId().intValue(),
                    checkNumber,
                    paymentMethod.toDbValue(),
                    finalPrice
            );

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Успешно");
            success.setHeaderText(null);
            success.setContentText("Пластинка продана!\nНомер чека: " + checkNumber);
            success.showAndWait();

            Stage stage = (Stage) checkNumberField.getScene().getWindow();
            stage.close();

        } catch (ValidationException e) {
            showAlert("Ошибка валидации", e.getMessage());
        } catch (InstanceNotAvailableException e) {
            showAlert("Ошибка", e.getMessage());
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось продать пластинку: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) checkNumberField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}