package group.lab6.lab6.controller;

import group.lab6.lab6.model.Instance;
import group.lab6.lab6.model.Genre;
import group.lab6.lab6.model.ConditionGrade;
import group.lab6.lab6.service.VinylService;
import group.lab6.lab6.service.CatalogService;
import group.lab6.lab6.service.PhotoService;
import group.lab6.lab6.service.ReferenceService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class SearchController {

    @FXML private TextField artistField;
    @FXML private TextField albumField;
    @FXML private ComboBox<Genre> genreComboBox;
    @FXML private ComboBox<String> conditionComboBox;
    @FXML private TableView<Instance> resultTable;
    @FXML private TableColumn<Instance, String> artistColumn;
    @FXML private TableColumn<Instance, String> albumColumn;
    @FXML private TableColumn<Instance, String> genreColumn;
    @FXML private TableColumn<Instance, String> conditionColumn;
    @FXML private TableColumn<Instance, String> priceColumn;
    @FXML private TableColumn<Instance, String> locationColumn;
    @FXML private TableColumn<Instance, String> formatColumn;
    @FXML private TableColumn<Instance, String> speedColumn;
    @FXML private Label statusLabel;

    private VinylService vinylService;
    private CatalogService catalogService;
    private PhotoService photoService;
    private ReferenceService referenceService;
    private Stage ownerStage;

    private ObservableList<Instance> instanceList = FXCollections.observableArrayList();

    public void setVinylService(VinylService vinylService) {
        this.vinylService = vinylService;
        handleSearch();
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public void setPhotoService(PhotoService photoService) {
        this.photoService = photoService;
    }

    public void setReferenceService(ReferenceService referenceService) {
        this.referenceService = referenceService;
        handleSearch();
    }

    public void setOwnerStage(Stage ownerStage) {
        this.ownerStage = ownerStage;
    }

    public void setSearchText(String text) {
        if (text != null && !text.trim().isEmpty()) {
            artistField.setText(text);
            handleSearch();
        }
    }

    @FXML
    public void initialize() {
        artistColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRelease().getArtist()));
        albumColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRelease().getAlbumTitle()));
        genreColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getRelease().getGenre() != null) {
                return new SimpleStringProperty(cellData.getValue().getRelease().getGenre().getGenreName());
            }
            return new SimpleStringProperty("");
        });
        conditionColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().isUsed() && cellData.getValue().getUsedDetails() != null) {
                return new SimpleStringProperty(cellData.getValue().getUsedDetails().getVinylCondition().getRusName());
            }
            return new SimpleStringProperty("Новый");
        });
        priceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f ₽", cellData.getValue().getPrice())));
        locationColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFullLocation()));
        formatColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFormat().getDisplayName()));
        speedColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSpeed().getDisplayName()));

        resultTable.setItems(instanceList);
    }

    @FXML
    private void handleSearch() {
        String artist = artistField.getText().trim();
        String album = albumField.getText().trim();
        String genreName = null;
        if (genreComboBox.getValue() != null) {
            genreName = genreComboBox.getValue().getGenreName();
        }
        String condition = conditionComboBox.getValue();

        if (condition != null && condition.equals("Новый")) {
            condition = null;
        }

        try {
            List<Instance> results = vinylService.searchInstances(artist, album, genreName, condition);
            instanceList.clear();
            instanceList.addAll(results);
            statusLabel.setText("Найдено: " + results.size());
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось выполнить поиск: " + e.getMessage());
        }
    }

    @FXML
    private void handleReset() {
        artistField.clear();
        albumField.clear();
        genreComboBox.setValue(null);
        conditionComboBox.setValue(null);
        instanceList.clear();
        statusLabel.setText("");
    }

    @FXML
    private void handleSell() {
        Instance selected = resultTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Ошибка", "Выберите пластинку для продажи");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/group/lab6/lab6/view/SellView.fxml"));
            Parent root = loader.load();

            SellController sellController = loader.getController();
            sellController.setVinylService(vinylService);
            sellController.setSelectedInstance(selected);
            sellController.setOwnerStage((Stage) resultTable.getScene().getWindow());

            Stage stage = new Stage();
            stage.setTitle("Продажа пластинки");
            stage.setScene(new Scene(root, 750, 350));
            stage.initOwner((Stage) resultTable.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();

            handleSearch();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно продажи");
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) resultTable.getScene().getWindow();
        stage.close();
    }

    public void loadGenres() {
        if (catalogService != null) {
            List<Genre> genres = catalogService.getAllGenres();
            genreComboBox.getItems().clear();
            genreComboBox.getItems().addAll(genres);
        }
    }

    public void loadConditions() {
        conditionComboBox.getItems().clear();
        conditionComboBox.getItems().add("Новый");
        for (ConditionGrade condition : ConditionGrade.values()) {
            conditionComboBox.getItems().add(condition.getRusName());
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