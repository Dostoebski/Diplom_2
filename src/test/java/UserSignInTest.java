import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.Register;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.*;

public class UserSignInTest extends BaseTest {

    private Register register;
    private String accessToken;

    @Before
    public void setUp() {
        register = Register.getDefaultRegister();
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка создания пользователя с уникальными данными")
    public void postRegisterValidCredentialsReturnsSuccess() {

        step("Отправить запрос на создание пользователя");
        Response response = burgersClient.createUser(register);

        step("Проверить, что статус ответа 200");
        int statusCode = response.then().extract().statusCode();
        assertEquals("Status code is not 200 OK", SC_OK, statusCode);

        step("Проверить, что в поле success true");
        boolean hasSuccess = response.then().extract().path("success");
        assertTrue(hasSuccess);

        step("Проверить, что вернулся access token");
        accessToken = response.then().extract().path("accessToken");
        assertNotNull(accessToken);

        step("Проверить, что вернулся refresh token");
        String refreshToken = response.then().extract().path("refreshToken");
        assertNotNull(refreshToken);
    }

    @Test
    @DisplayName("Создание существуещего пользователя")
    @Description("Проверка попытки создать пользователя, который уже зарегистрирован")
    public void postRegisterAlreadyExistedUserReturnsFault() {

        step("Создать нового пользователя");
        accessToken = burgersClient.createUser(register).then().extract().path("accessToken");

        step("Попытаться снова создать этого пользователя");
        Response response = burgersClient.createUser(register);

        step("Проверить, что статус ответа 403");
        int statusCode = response.then().extract().statusCode();
        assertEquals("Status code is not 403 FORBIDDEN", SC_FORBIDDEN, statusCode);

        step("Проверить поле success false");
        boolean hasSuccess = response.then().extract().path("success");
        assertFalse(hasSuccess);

        step("Проверить поле message");
        String message = response.then().extract().path("message");
        String expected = "User already exists";
        assertEquals("Error message doesn't match", expected, message);
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Проверка попытки создать пользователя без не заполнения одного из обязательных полей")
    public void postRegisterWithoutEmailReturnsFault() {

        step("Попытаться создать пользователя без email");
        register.setEmail(null);
        Response response = burgersClient.createUser(register);
        accessToken = response.then().extract().path("accessToken");

        step("Проверить, что статус ответа 403");
        int statusCode = response.then().extract().statusCode();
        assertEquals("Status code is not 403 FORBIDDEN", SC_FORBIDDEN, statusCode);

        step("Проверить, что в поле success false");
        boolean hasSuccess = response.then().extract().path("success");
        assertFalse(hasSuccess);

        step("Проверить поле message");
        String message = response.then().extract().path("message");
        String expected = "Email, password and name are required fields";
        assertEquals("Error message doesn't match", expected, message);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            burgersClient.deleteUser(accessToken);
        }
    }
}
