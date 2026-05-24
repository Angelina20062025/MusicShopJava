package group.lab6.lab6;

import group.lab6.lab6.controller.LoginController;
import group.lab6.lab6.service.AuthService;
import group.lab6.lab6.service.impl.AuthServiceImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    private AuthService authService;

    @Override
    public void start(Stage primaryStage) throws Exception {
        authService = new AuthServiceImpl();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/group/lab6/lab6/view/login-view.fxml"));
        Parent root = loader.load();

        LoginController loginController = loader.getController();
        loginController.setPrimaryStage(primaryStage);
        loginController.setAuthService(authService);

        primaryStage.setTitle("Вход в систему");
        primaryStage.setScene(new Scene(root, 350, 150));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        if (authService != null && authService.isAuthenticated()) {
            authService.logout();
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
