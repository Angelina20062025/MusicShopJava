package group.lab6.lab6.controller;

import group.lab6.lab6.dao.DatabaseConnection;
import group.lab6.lab6.dao.impl.*;
import group.lab6.lab6.service.*;
import group.lab6.lab6.service.impl.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private TextField quickSearchField;

    @FXML
    private Parent mainRoot;

    @FXML
    private Label userLabel;

    private VinylService vinylService;
    private CatalogService catalogService;
    private ReferenceService referenceService;
    private ReportService reportService;
    private PhotoService photoService;
    private AuthService authService;

    @FXML
    public void initialize() {
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();

        ReleaseDAOImpl releaseDAO = new ReleaseDAOImpl(dbConnection);
        InstanceDAOImpl instanceDAO = new InstanceDAOImpl(dbConnection);
        SupplierDAOImpl supplierDAO = new SupplierDAOImpl(dbConnection);
        SaleDAOImpl saleDAO = new SaleDAOImpl(dbConnection);
        PhotoDAOImpl photoDAO = new PhotoDAOImpl(dbConnection);
        DefectPhotoDAOImpl defectPhotoDAO = new DefectPhotoDAOImpl(dbConnection);

        this.vinylService = new VinylServiceImpl(instanceDAO, releaseDAO, photoDAO);
        this.catalogService = new CatalogServiceImpl(releaseDAO, instanceDAO);
        this.referenceService = new ReferenceServiceImpl(supplierDAO);
        this.reportService = new ReportServiceImpl(saleDAO);
        this.photoService = new PhotoServiceImpl(photoDAO, defectPhotoDAO);

        String currentUser = DatabaseConnection.getCurrentUser();
        if (currentUser != null) {
            userLabel.setText("Пользователь: " + currentUser);
        }
    }

    @FXML
    private void handleExit() {
        DatabaseConnection.getInstance().closeConnection();
        System.exit(0);
    }

    @FXML
    private void handleAddNewInstance() {
        openWindow("/group/lab6/lab6/view/AddNewInstanceView.fxml", "Добавление новой пластинки");
    }

    @FXML
    private void handleAddUsedInstance() {
        openWindow("/group/lab6/lab6/view/AddUsedInstanceView.fxml", "Добавление Б/У пластинки");
    }

    @FXML
    private void handleSell() {
        handleSearch();
    }

    @FXML
    private void handleManageSuppliers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/group/lab6/lab6/view/SupplierView.fxml"));
            Parent root = loader.load();

            SupplierController controller = loader.getController();
            controller.setReferenceService(referenceService);

            Stage stage = new Stage();
            stage.setTitle("Управление поставщиками");
            stage.setScene(new Scene(root, 700, 500));
            Stage ownerStage = (Stage) mainRoot.getScene().getWindow();
            stage.initOwner(ownerStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть окно поставщиков");
        }
    }

    @FXML
    private void handleSearch() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/group/lab6/lab6/view/SearchView.fxml"));
            Parent root = loader.load();

            SearchController searchController = loader.getController();
            searchController.setVinylService(vinylService);
            searchController.setCatalogService(catalogService);
            searchController.setPhotoService(photoService);
            searchController.setReferenceService(referenceService);
            searchController.loadGenres();
            searchController.loadConditions();

            Stage stage = new Stage();
            stage.setTitle("Поиск пластинок");
            stage.setScene(new Scene(root, 900, 550));
            stage.initOwner((Stage) mainRoot.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть окно поиска");
        }
    }

    @FXML
    private void handleReports() {
        openWindow("/group/lab6/lab6/view/ReportView.fxml", "Отчёты");
    }

    @FXML
    private void handleQuickSearch() {
        String searchText = quickSearchField.getText();
        if (searchText == null || searchText.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Введите текст для поиска");
            return;
        }

        openSearchWithText(searchText);
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("О программе");
        alert.setHeaderText("АРМ Менеджера Винилового Магазина");
        alert.setContentText("Разработано для учёта виниловых пластинок");
        alert.showAndWait();
    }

    @FXML
    private void handleChangeLanguage() {
        // TODO: реализовать переключение языка
        showAlert(Alert.AlertType.INFORMATION, "Язык", "Функция будет добавлена позже");
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
        String currentUser = authService.getCurrentUser();
        if (currentUser != null && userLabel != null) {
            userLabel.setText("Пользователь: " + currentUser);
        }
    }

    private void openWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            Stage ownerStage = (Stage) mainRoot.getScene().getWindow();
            stage.initOwner(ownerStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть окно: " + e.getMessage());
        }
    }

    private void openSearchWithText(String searchText) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/group/lab6/lab6/view/SearchView.fxml"));
            Parent root = loader.load();

            SearchController controller = loader.getController();
            controller.setVinylService(vinylService);
            controller.setCatalogService(catalogService);
            controller.setPhotoService(photoService);
            controller.setReferenceService(referenceService);
            controller.loadGenres();
            controller.loadConditions();
            controller.setSearchText(searchText);

            Stage stage = new Stage();
            stage.setTitle("Поиск пластинок");
            stage.setScene(new Scene(root, 900, 550));
            stage.initOwner((Stage) mainRoot.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть окно поиска");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}