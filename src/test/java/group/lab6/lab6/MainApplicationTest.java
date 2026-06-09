package group.lab6.lab6;

import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import static org.testfx.util.WaitForAsyncUtils.waitFor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

public class MainApplicationTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        new MainApplication().start(stage);
    }

    @Test
    @DisplayName("Успешный вход в систему")
    void testSuccessfulLogin() throws Exception {
        clickOn("#loginField").write("manager_user");
        clickOn("#passwordField").write("manager2026shop");

        clickOn("#handleLogin");

        waitFor(5, TimeUnit.SECONDS, () -> lookup("#resultTable").tryQuery().isPresent());

        TableView<?> tableView = lookup("#resultTable").query();
        assertThat(tableView).isNotNull();
    }

    @Test
    @DisplayName("Открытие окна поставщиков")
    void testOpenSuppliersWindow() throws Exception {
        clickOn("#loginField").write("manager_user");
        clickOn("#passwordField").write("manager2026shop");
        clickOn("#handleLogin");

        waitFor(3, TimeUnit.SECONDS, () -> lookup("#resultTable").query().isVisible());
        clickOn("Операции");
        clickOn("Управление поставщиками");
        waitFor(3, TimeUnit.SECONDS, () -> lookup("#supplierTable").tryQuery().isPresent());
        lookup("#supplierTable").query();
    }

    @Test
    @DisplayName("Вход с неверным паролем")
    void testLoginWithWrongPassword() throws Exception {
        clickOn("#loginField").write("manager_user");
        clickOn("#passwordField").write("123");

        clickOn("#handleLogin");

        waitFor(5, TimeUnit.SECONDS, () -> lookup(".dialog-pane").tryQuery().isPresent());

        assertThat(lookup(".dialog-pane").tryQuery().isPresent()).isTrue();

        clickOn("OK");

        boolean dialogPresent = lookup(".dialog-pane").tryQuery().isPresent();
        assertThat(dialogPresent).isFalse();
        assertThat(lookup("#resultTable").tryQuery().isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Продажа без выбора пластинки")
    void testSellWithoutSelection() throws Exception {
        clickOn("#loginField").write("manager_user");
        clickOn("#passwordField").write("manager2026shop");
        clickOn("#handleLogin");

        waitFor(3, java.util.concurrent.TimeUnit.SECONDS,
                () -> lookup("#resultTable").tryQuery().isPresent());

        clickOn("Продать");

        waitFor(3, java.util.concurrent.TimeUnit.SECONDS,
                () -> lookup(".dialog-pane").tryQuery().isPresent());

        assertThat(lookup(".dialog-pane").tryQuery().isPresent()).isTrue();

        assertThat(lookup(".dialog-pane .content").query().isVisible()).isTrue();

        clickOn("OK");
    }
}