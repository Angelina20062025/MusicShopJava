package group.lab6.lab6.controller;

import group.lab6.lab6.model.SellerPerson;
import group.lab6.lab6.service.ReferenceService;
import group.lab6.lab6.service.exceptions.ValidationException;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SellerPersonController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField patronymicField;
    @FXML private TextField phoneField;
    @FXML private TableView<SellerPerson> sellerTable;
    @FXML private TableColumn<SellerPerson, Long> idColumn;
    @FXML private TableColumn<SellerPerson, String> firstNameColumn;
    @FXML private TableColumn<SellerPerson, String> lastNameColumn;
    @FXML private TableColumn<SellerPerson, String> patronymicColumn;
    @FXML private TableColumn<SellerPerson, String> phoneColumn;
    @FXML private Label statusLabel;

    private ReferenceService referenceService;
    private ObservableList<SellerPerson> sellerList = FXCollections.observableArrayList();

    public void setReferenceService(ReferenceService referenceService) {
        this.referenceService = referenceService;
        handleLoadSellers();
    }

    @FXML
    private void handleLoadSellers() {
        if (referenceService == null) {
            showAlert("Ошибка", "Сервис не инициализирован");
            return;
        }

        try {
            sellerList.clear();
            sellerList.addAll(referenceService.getAllSellers());
            sellerTable.setItems(sellerList);
            statusLabel.setText("Загружено продавцов: " + sellerList.size());
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось загрузить продавцов: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getPersonId()).asObject());
        firstNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));
        lastNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));
        patronymicColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPatronymic()));
        phoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));

        sellerTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        firstNameField.setText(newSelection.getFirstName());
                        lastNameField.setText(newSelection.getLastName());
                        patronymicField.setText(newSelection.getPatronymic());
                        phoneField.setText(newSelection.getPhone());
                    }
                });
        idColumn.setVisible(false);
    }

    @FXML
    private void handleAdd() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String patronymic = patronymicField.getText().trim();
        String phone = phoneField.getText().trim();

        if (firstName.isEmpty()) {
            showAlert("Ошибка", "Введите имя");
            return;
        }
        if (lastName.isEmpty()) {
            showAlert("Ошибка", "Введите фамилию");
            return;
        }
        if (phone.isEmpty()) {
            showAlert("Ошибка", "Введите телефон");
            return;
        }

        try {
            validatePhone(phone);
            SellerPerson newSeller = referenceService.addSeller(firstName, lastName, patronymic, phone);
            sellerList.add(newSeller);
            handleClearForm();
            statusLabel.setText("Продавец \"" + lastName + " " + firstName + "\" добавлен");
        } catch (ValidationException e) {
            showAlert("Ошибка валидации", e.getMessage());
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось добавить продавца: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        SellerPerson selected = sellerTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Ошибка", "Выберите продавца для обновления");
            return;
        }

        String newFirstName = firstNameField.getText().trim();
        String newLastName = lastNameField.getText().trim();
        String newPatronymic = patronymicField.getText().trim();
        String newPhone = phoneField.getText().trim();

        if (newFirstName.isEmpty()) {
            showAlert("Ошибка", "Введите имя");
            return;
        }
        if (newLastName.isEmpty()) {
            showAlert("Ошибка", "Введите фамилию");
            return;
        }
        if (newPhone.isEmpty()) {
            showAlert("Ошибка", "Введите телефон");
            return;
        }

        try {
            validatePhone(newPhone);
            selected.setFirstName(newFirstName);
            selected.setLastName(newLastName);
            selected.setPatronymic(newPatronymic);
            selected.setPhone(newPhone);
            referenceService.updateSeller(selected);
            sellerTable.refresh();
            statusLabel.setText("Продавец обновлён");
        } catch (ValidationException e) {
            showAlert("Ошибка валидации", e.getMessage());
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось обновить продавца: " + e.getMessage());
        }
    }

    @FXML
    private void handleArchive() {
        SellerPerson selected = sellerTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Ошибка", "Выберите продавца для архивации");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText("Архивация продавца");
        confirm.setContentText("Вы уверены, что хотите заархивировать продавца \"" + selected.getLastName() + " " + selected.getFirstName() + "\"?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    referenceService.archiveSeller(selected.getPersonId().intValue());
                    sellerList.remove(selected);
                    handleClearForm();
                    statusLabel.setText("Продавец заархивирован");
                } catch (Exception e) {
                    showAlert("Ошибка", "Не удалось заархивировать продавца: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleClearForm() {
        firstNameField.clear();
        lastNameField.clear();
        patronymicField.clear();
        phoneField.clear();
        sellerTable.getSelectionModel().clearSelection();
    }

    private void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new ValidationException("Введите номер телефона");
        }

        String digitsOnly = phone.replaceAll("\\D", "");

        if (digitsOnly.length() != 11) {
            throw new ValidationException("Номер телефона должен содержать 11 цифр");
        }
    }

//    private void validatePhone(String phone) {
//        if (phone == null || phone.trim().isEmpty()) {
//            throw new ValidationException("Введите номер телефона");
//        }
//        long digitCount = phone.chars().filter(Character::isDigit).count();
//        if (!phone.matches("^[\\+\\d\\s\\-\\(\\)\\/]+$")) {
//            throw new ValidationException("Телефон должен содержать только цифры и символы +, -, (, ), /");
//        }
//        if (digitCount < 11) {
//            throw new ValidationException("Некорректный номер телефона");
//        }
//    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}