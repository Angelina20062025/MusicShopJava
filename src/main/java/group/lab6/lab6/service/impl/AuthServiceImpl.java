package group.lab6.lab6.service.impl;

import group.lab6.lab6.dao.DatabaseConnection;
import group.lab6.lab6.service.AuthService;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private String currentUser;
    private boolean authenticated = false;

    @Override
    public boolean authenticate(String login, String password) {
        logger.info("Попытка аутентификации пользователя: {}", login);
        try {
            DatabaseConnection.setCredentials(login, password);
            DatabaseConnection.getInstance().getConnection();

            this.currentUser = login;
            this.authenticated = true;
            logger.info("Пользователь {} успешно аутентифицирован", login);
            return true;

        } catch (SQLException e) {
            this.authenticated = false;
            this.currentUser = null;
            logger.error("Ошибка аутентификации пользователя {}: {}", login, e.getMessage());
            return false;
        }
    }

    @Override
    public void logout() {
        DatabaseConnection.getInstance().closeConnection();
        this.currentUser = null;
        this.authenticated = false;
    }

    @Override
    public String getCurrentUser() {
        return currentUser;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }
}