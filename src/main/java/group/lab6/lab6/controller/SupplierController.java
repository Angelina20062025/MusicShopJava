package group.lab6.lab6.controller;

import group.lab6.lab6.model.Supplier;
import group.lab6.lab6.service.ReferenceService;
import group.lab6.lab6.service.exceptions.ValidationException;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SupplierController {

    @FXML private TextField nameField;
    @FXML private TextArea contactField;
    @FXML private TableView<Supplier> supplierTable;
    @FXML private TableColumn<Supplier, Long> idColumn;
    @FXML private TableColumn<Supplier, String> nameColumn;
    @FXML private TableColumn<Supplier, String> contactColumn;
    @FXML private Label statusLabel;

    private ReferenceService referenceService;
    private ObservableList<Supplier> supplierList = FXCollections.observableArrayList();

    public void setReferenceService(ReferenceService referenceService) {
        this.referenceService = referenceService;
        handleLoadSuppliers();
    }

    @FXML
    private void handleLoadSuppliers() {
        if (referenceService == null) {
            showAlert("Ошибка", "Сервис не инициализирован");
            return;
        }

        try {
            supplierList.clear();
            supplierList.addAll(referenceService.getAllSuppliers());
            supplierTable.setItems(supplierList);
            statusLabel.setText("Загружено поставщиков: " + supplierList.size());
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось загрузить поставщиков: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getSupplierId()).asObject());
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        contactColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContactInfo()));

        supplierTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        nameField.setText(newSelection.getName());
                        contactField.setText(newSelection.getContactInfo());
                    }
                });
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();

        if (name.isEmpty()) {
            showAlert("Ошибка", "Название поставщика не может быть пустым");
            return;
        }

        try {
            Supplier newSupplier = referenceService.addSupplier(name, contact);
            supplierList.add(newSupplier);
            handleClearForm();
            statusLabel.setText("Поставщик \"" + name + "\" добавлен");
        } catch (ValidationException e) {
            showAlert("Ошибка валидации", e.getMessage());
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось добавить поставщика: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Supplier selected = supplierTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Ошибка", "Выберите поставщика для обновления");
            return;
        }

        String newName = nameField.getText().trim();
        String newContact = contactField.getText().trim();

        if (newName.isEmpty()) {
            showAlert("Ошибка", "Название поставщика не может быть пустым");
            return;
        }

        try {
            selected.setName(newName);
            selected.setContactInfo(newContact);
            referenceService.updateSupplier(selected);
            supplierTable.refresh();
            statusLabel.setText("Поставщик обновлён");
        } catch (ValidationException e) {
            showAlert("Ошибка валидации", e.getMessage());
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось обновить поставщика: " + e.getMessage());
        }
    }

    @FXML
    private void handleArchive() {
        Supplier selected = supplierTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Ошибка", "Выберите поставщика для архивации");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText("Архивация поставщика");
        confirm.setContentText("Вы уверены, что хотите заархивировать поставщика \"" + selected.getName() + "\"?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    referenceService.archiveSupplier(selected.getSupplierId().intValue());
                    supplierList.remove(selected);
                    handleClearForm();
                    statusLabel.setText("Поставщик заархивирован");
                } catch (Exception e) {
                    showAlert("Ошибка", "Не удалось заархивировать поставщика: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleClearForm() {
        nameField.clear();
        contactField.clear();
        supplierTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}