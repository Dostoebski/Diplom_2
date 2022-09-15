import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.Register;
import model.RegisterFactory;
import model.User;
import model.UserFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.*;

public class UserUpdateTest extends BaseTest {

    private static String accessToken;
    private static User user;

    @Before
    public void setUp() {
        Register register = RegisterFactory.getDefaultRegister();
        accessToken = burgersClient.createUser(register).then().extract().path("accessToken");
        user = UserFactory.getUser(register);
    }

    @Test
    @DisplayName("Изменение email")
    @Description("Проверка изменения электронной почты пользователя")
    public void patchUserEmailReturnsSuccess() {
        user.setEmail("new-email@example.com");

        step("Отправить запрос на изменение данных");
        Response response = burgersClient.changeUser(user, accessToken);

        step("Проверить, что статус ответа 200");
        int statusCode = response.then().extract().statusCode();
        assertEquals("Status code is not 200 OK", SC_OK, statusCode);

        step("Проверить, что в поле success true");
        boolean hasSuccess = response.then().extract().path("success");
        assertTrue(hasSuccess);

        step("Проверить, что почта в ответе верная");
        String email = response.then().extract().path("user.email");
        assertEquals("User email doesn't match", user.getEmail(), email);

        step("Проверить, что имя пользователя в ответе верное");
        String name = response.then().extract().path("user.name");
        assertEquals("User name doesn't match", user.getName(), name);

    }

    @Test
    @DisplayName("Изменение имени")
    @Description("Проверка изменения имени пользователя")
    public void patchUserNameReturnsSuccess() {
        user.setName("NewName");

        step("Отправить запрос на изменение данных");
        Response response = burgersClient.changeUser(user, accessToken);

        step("Проверить, что статус ответа 200");
        int statusCode = response.then().extract().statusCode();
        assertEquals("Status code is not 200 OK", SC_OK, statusCode);

        step("Проверить, что в поле success true");
        boolean hasSuccess = response.then().extract().path("success");
        assertTrue(hasSuccess);

        step("Проверить, что почта в ответе верная");
        String email = response.then().extract().path("user.email");
        assertEquals("User email doesn't match", user.getEmail(), email);

        step("Проверить, что имя пользователя в ответе верное");
        String name = response.then().extract().path("user.name");
        assertEquals("User name doesn't match", user.getName(), name);
    }

    @Test
    @DisplayName("Неавторизованное изменение данных пользователя")
    @Description("Проверка попытки изменить данные пользователя без авторизации")
    public void patchUserUnauthorizedReturnsFault() {

        step("Отправить запрос на изменение данных без авторизации");
        Response response = burgersClient.changeUser(user);

        step("Проверить, что статус ответа 401");
        int statusCode = response.then().extract().statusCode();
        assertEquals("Status code is not 401 UNAUTHORIZED", SC_UNAUTHORIZED, statusCode);

        step("Проверить, что в поле success false");
        boolean hasSuccess = response.then().extract().path("success");
        assertFalse(hasSuccess);

        step("Проверить поле message");
        String message = response.then().extract().path("message");
        String expected = "You should be authorised";
        assertEquals("Error message doesn't match", expected, message);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            burgersClient.deleteUser(accessToken);
        }
    }
}
