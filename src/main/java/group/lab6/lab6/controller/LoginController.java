package group.lab6.lab6.controller;

import group.lab6.lab6.service.AuthService;
import group.lab6.lab6.service.impl.AuthServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    private AuthService authService;
    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @FXML
    private void handleLogin() {
        String login = loginField.getText().trim();
        String password = passwordField.getText();

        if (login.isEmpty()) {
            showAlert("Ошибка", "Введите логин");
            return;
        }

        if (password.isEmpty()) {
            showAlert("Ошибка", "Введите пароль");
            return;
        }

        boolean success = authService.authenticate(login, password);

        if (success) {
            openMainWindow();
        } else {
            showAlert("Ошибка аутентификации", "Неверный логин или пароль");
            loginField.clear();
            passwordField.clear();
            loginField.requestFocus();
        }
    }

    @FXML
    private void handleCancel() {
        System.exit(0);
    }

    private void openMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/group/lab6/lab6/view/main-view.fxml"));
            Parent root = loader.load();

            MainController mainController = loader.getController();
            mainController.setAuthService(authService);

            Scene scene = new Scene(root);
            primaryStage.setTitle("АРМ Менеджера Винилового Магазина");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Критическая ошибка", "Не удалось загрузить главное окно: " + e.getMessage());
            System.exit(1);
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