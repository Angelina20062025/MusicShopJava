package group.lab6.lab6.service;

/**
 * Предоставляет методы для аутентификации пользователей через механизмы СУБД.
 * Логин и пароль не хранятся в приложении, а передаются непосредственно в PostgreSQL.
 */
public interface AuthService {
    boolean authenticate(String login, String password);
    void logout();
    String getCurrentUser();
    boolean isAuthenticated();
}