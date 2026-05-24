package group.lab6.lab6.service;

public interface AuthService {
    boolean authenticate(String login, String password);
    void logout();
    String getCurrentUser();
    boolean isAuthenticated();
}