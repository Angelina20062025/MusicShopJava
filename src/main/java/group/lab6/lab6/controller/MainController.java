package group.lab6.lab6.controller;

import group.lab6.lab6.dao.DatabaseConnection;
import group.lab6.lab6.dao.impl.*;
import group.lab6.lab6.model.Genre;
import group.lab6.lab6.model.Instance;
import group.lab6.lab6.model.PaymentMethod;
import group.lab6.lab6.service.*;
import group.lab6.lab6.service.exceptions.InstanceNotAvailableException;
import group.lab6.lab6.service.exceptions.ValidationException;
import group.lab6.lab6.service.impl.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MainController {

    @FXML
    private TextField quickSearchField;

    @FXML
    private Parent mainRoot;

    @FXML
    private Label userLabel;

    @FXML
    private ComboBox<PaymentMethod> paymentMethodComboBox;

    @FXML
    private TableView<Instance> availableTable;

    @FXML
    private TableView<Instance> resultTable;

    @FXML
    private TableColumn<Instance, String> artistColumn;

    @FXML
    private TableColumn<Instance, String> albumColumn;

    @FXML
    private TableColumn<Instance, String> genreColumn;

    @FXML
    private TableColumn<Instance, String> conditionColumn;

    @FXML
    private TableColumn<Instance, String> priceColumn;

    @FXML
    private ComboBox<Genre> genreFilterComboBox;

    @FXML
    private ComboBox<String> conditionFilterComboBox;

    @FXML
    private TableColumn<Instance, String> locationColumn;

    @FXML
    private TableColumn<Instance, String> formatColumn;

    @FXML
    private TableColumn<Instance, String> speedColumn;

    private ObservableList<Instance> instanceList = FXCollections.observableArrayList();
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
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
        SellerPersonDAOImpl sellerPersonDAO = new SellerPersonDAOImpl(dbConnection);

        this.referenceService = new ReferenceServiceImpl(supplierDAO, sellerPersonDAO);
        this.vinylService = new VinylServiceImpl(instanceDAO, releaseDAO, photoDAO);
        this.catalogService = new CatalogServiceImpl(releaseDAO, instanceDAO);
        this.reportService = new ReportServiceImpl(saleDAO);
        this.photoService = new PhotoServiceImpl(photoDAO, defectPhotoDAO);

        String currentUser = DatabaseConnection.getCurrentUser();
        if (currentUser != null) {
            userLabel.setText("Пользователь: " + currentUser);
        }

        paymentMethodComboBox.getItems().setAll(PaymentMethod.values());
        paymentMethodComboBox.setValue(PaymentMethod.CASH);

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
        handleRefreshAvailable();

        if (catalogService != null) {
            genreFilterComboBox.getItems().addAll(catalogService.getAllGenres());
        }
        conditionFilterComboBox.getItems().addAll("Идеальное", "Почти идеальное", "Очень хорошее", "Хорошее", "Удовлетворительное", "Плохое");
        conditionFilterComboBox.setValue(null);
    }

    @FXML
    private void handleEditInstance() {
        Instance selected = resultTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Выберите пластинку в таблице");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/group/lab6/lab6/view/EditInstanceView.fxml"));
            Parent root = loader.load();

            EditInstanceController controller = loader.getController();
            controller.setVinylService(vinylService);
            controller.setInstance(selected);

            Stage stage = new Stage();
            stage.setTitle("Редактирование экземпляра");
            stage.setScene(new Scene(root, 500, 450));
            stage.initOwner((Stage) mainRoot.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();

            handleRefreshAvailable();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть окно редактирования");
        }
    }

    @FXML
    private void handleResetFilters() {
        quickSearchField.clear();
        genreFilterComboBox.setValue(null);
        conditionFilterComboBox.setValue(null);
        instanceList.clear();
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
            stage.initOwner((Stage) mainRoot.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть окно управления продавцами");
        }
    }

    @FXML
    private void handleArchive() {
        Instance selected = resultTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Выберите пластинку в таблице");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText(null);
        confirm.setContentText("Заархивировать пластинку?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    vinylService.archiveInstance(selected.getInstanceId().intValue());
                    showAlert(Alert.AlertType.INFORMATION, "Успех", "Пластинка заархивирована");
                    handleRefreshAvailable();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось заархивировать: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleSellFromMain() {
        Instance selected = resultTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите пластинку в таблице");
            return;
        }
        logger.info("Пользователь выбрал пластинку для продажи: ID={}", selected.getInstanceId());
        if (selected == null) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите пластинку в таблице для продажи");
            return;
        }

        PaymentMethod paymentMethod = paymentMethodComboBox.getValue();
        if (paymentMethod == null) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите способ оплаты");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение продажи");
        confirm.setHeaderText("Вы уверены, что хотите продать эту пластинку?");
        confirm.setContentText(
                String.format("Исполнитель: %s\nАльбом: %s\nЦена: %.2f ₽\nСпособ оплаты: %s",
                        selected.getRelease().getArtist(),
                        selected.getRelease().getAlbumTitle(),
                        selected.getPrice(),
                        paymentMethod.getRusName()
                )
        );

        ButtonType result = confirm.showAndWait().orElse(ButtonType.CANCEL);
        if (result != ButtonType.OK) {
            return;
        }

        String checkNumber = "CHK-" + System.currentTimeMillis();

        try {
            vinylService.sellInstance(
                    selected.getInstanceId().intValue(),
                    checkNumber,
                    paymentMethod.toDbValue(),
                    selected.getPrice()
            );

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Успешно");
            success.setHeaderText(null);
            success.setContentText("Пластинка продана!\nНомер чека: " + checkNumber);
            success.showAndWait();

            handleRefreshAvailable();

        } catch (ValidationException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка валидации", e.getMessage());
        } catch (InstanceNotAvailableException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось продать пластинку: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefreshAvailable() {
        try {
            List<Instance> results = vinylService.searchInstances(null, null, null, null);
            instanceList.clear();
            instanceList.addAll(results);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить список: " + e.getMessage());
        }
    }

    @FXML
    private void handleExit() {
        DatabaseConnection.getInstance().closeConnection();
        System.exit(0);
    }

    @FXML
    private void handleAddNewInstance() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/group/lab6/lab6/view/AddNewInstanceView.fxml"));
            Parent root = loader.load();

            AddNewInstanceController controller = loader.getController();
            controller.setVinylService(vinylService);
            controller.setCatalogService(catalogService);
            controller.setReferenceService(referenceService);

            Stage stage = new Stage();
            stage.setTitle("Добавление новой пластинки");
            stage.setScene(new Scene(root, 600, 800));
            stage.initOwner((Stage) mainRoot.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();

            handleRefreshAvailable();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть окно");
        }
    }

    @FXML
    private void handleAddUsedInstance() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/group/lab6/lab6/view/AddUsedInstanceView.fxml"));
            Parent root = loader.load();

            AddUsedInstanceController controller = loader.getController();
            controller.setVinylService(vinylService);
            controller.setCatalogService(catalogService);
            controller.setReferenceService(referenceService);

            Stage stage = new Stage();
            stage.setTitle("Добавление Б/У пластинки");
            stage.setScene(new Scene(root, 600, 800));
            stage.initOwner((Stage) mainRoot.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();

            handleRefreshAvailable();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть окно добавления Б/У пластинки");
        }
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

//    @FXML
//    private void handleSearch() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/group/lab6/lab6/view/SearchView.fxml"));
//            Parent root = loader.load();
//
//            SearchController searchController = loader.getController();
//            searchController.setVinylService(vinylService);
//            searchController.setCatalogService(catalogService);
//            searchController.setPhotoService(photoService);
//            searchController.setReferenceService(referenceService);
//            searchController.loadGenres();
//            searchController.loadConditions();
//
//            Stage stage = new Stage();
//            stage.setTitle("Поиск пластинок");
//            stage.setScene(new Scene(root, 900, 550));
//            stage.initOwner((Stage) mainRoot.getScene().getWindow());
//            stage.initModality(Modality.WINDOW_MODAL);
//            stage.show();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть окно поиска");
//        }
//    }

    @FXML
    private void handleReports() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/group/lab6/lab6/view/ReportView.fxml"));
            Parent root = loader.load();

            ReportController controller = loader.getController();
            controller.setReportService(reportService);

            Stage stage = new Stage();
            stage.setTitle("Отчёты");
            stage.setScene(new Scene(root, 900, 600));
            stage.initOwner((Stage) mainRoot.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть окно отчётов");
        }
    }

    @FXML
    private void handleManagePhotos() {
        Instance selected = resultTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Выберите пластинку в таблице");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/group/lab6/lab6/view/PhotoManagerView.fxml"));
            Parent root = loader.load();

            PhotoManagerController controller = loader.getController();
            controller.setPhotoService(photoService);
            controller.setVinylService(vinylService);
            controller.setInstance(selected);

            Stage stage = new Stage();
            stage.setTitle("Фото " + selected.getRelease().getArtist() + " - " + selected.getRelease().getAlbumTitle());
            stage.setScene(new Scene(root, 600, 550));
            stage.initOwner((Stage) mainRoot.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();

            handleRefreshAvailable();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть окно управления фото");
        }
    }

    @FXML
    private void handleQuickSearch() {
        String searchText = quickSearchField.getText();
        logger.info("Поиск пластинок по запросу: {}", searchText);
//        if (searchText == null || searchText.trim().isEmpty()) {
//            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Введите текст для поиска");
//            return;
//        }

        try {
            String genreName = genreFilterComboBox.getValue() != null ? genreFilterComboBox.getValue().getGenreName() : null;

            String condition = conditionFilterComboBox.getValue();
            String dbCondition = null;
            if (condition != null) {
                switch (condition) {
                    case "Идеальное": dbCondition = "mint"; break;
                    case "Почти идеальное": dbCondition = "near_mint"; break;
                    case "Очень хорошее": dbCondition = "very_good"; break;
                    case "Хорошее": dbCondition = "good"; break;
                    case "Удовлетворительное": dbCondition = "fair"; break;
                    case "Плохое": dbCondition = "poor"; break;
                }
            }

            List<Instance> results = vinylService.searchInstances(searchText, null, genreName, dbCondition);
            instanceList.clear();
            instanceList.addAll(results);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось выполнить поиск: " + e.getMessage());
        }
    }

//    @FXML
//    private void handleQuickSearch() {
//        String searchText = quickSearchField.getText();
//        if (searchText == null || searchText.trim().isEmpty()) {
//            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Введите текст для поиска");
//            return;
//        }
//
//        try {
//            String genreName = genreFilterComboBox.getValue() != null ? genreFilterComboBox.getValue().getGenreName() : null;
//            String condition = conditionFilterComboBox.getValue();
//            String dbCondition = mapConditionToDb(condition);
//            if (condition != null && condition.equals("Новый")) {
//                condition = null;
//            }
//
//            List<Instance> results = vinylService.searchInstances(searchText, null, genreName, condition);
//            instanceList.clear();
//            instanceList.addAll(results);
//        } catch (Exception e) {
//            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось выполнить поиск: " + e.getMessage());
//        }
//    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("О программе");
        alert.setHeaderText("АРМ Менеджера Винилового Магазина");
        alert.setContentText("Учёт виниловых пластинок");
        alert.showAndWait();
    }

    @FXML
    private void handleChangeLanguage() {
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

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}