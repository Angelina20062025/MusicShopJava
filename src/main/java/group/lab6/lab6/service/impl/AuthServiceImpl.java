package group.lab6.lab6.service.impl;

import group.lab6.lab6.dao.DatabaseConnection;
import group.lab6.lab6.service.AuthService;
import java.sql.SQLException;

public class AuthServiceImpl implements AuthService {

    private String currentUser;
    private boolean authenticated = false;

    @Override
    public boolean authenticate(String login, String password) {
        try {
            DatabaseConnection.setCredentials(login, password);
            DatabaseConnection.getInstance().getConnection();

            this.currentUser = login;
            this.authenticated = true;
            return true;

        } catch (SQLException e) {
            this.authenticated = false;
            this.currentUser = null;
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