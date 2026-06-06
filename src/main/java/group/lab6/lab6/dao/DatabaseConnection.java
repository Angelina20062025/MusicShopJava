package group.lab6.lab6.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private static String url = "jdbc:postgresql://localhost:5432/vinyl_store_manager";
    private static String user;
    private static String password;

    private DatabaseConnection() {}

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

//    public boolean testConnection() {
//        try {
//            Connection conn = getConnection();
//            return conn != null && !conn.isClosed();
//        } catch (SQLException e) {
//            return false;
//        }
//    }

    public static void setCredentials(String login, String pass) {
        user = login;
        password = pass;
    }

    public static String getCurrentUser() {
        return user;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
