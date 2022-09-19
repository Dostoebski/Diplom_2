import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.Register;
import model.UserCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

public class UserLogInTest extends BaseTest {

    private UserCredentials userCredentials;
    private Register register;
    private String accessToken;

    @Before
    public void setUp() {
        register = Register.getDefaultRegister();
        userCredentials = UserCredentials.getCredentials(register);
        accessToken = burgersClient.createUser(register).then().extract().path("accessToken");
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    @Description("Проверка входа с валидными данными")
    public void postLoginValidCredentialsReturnsSuccess() {

        step("Авторизоваться под существующим пользователем");
        Response response = burgersClient.login(userCredentials);

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

        step("Проверить, что почта в ответе верная");
        String email = response.then().extract().path("user.email");
        assertEquals("User email doesn't match", register.getEmail(), email);

        step("Проверить, что имя пользователя в ответе верное");
        String name = response.then().extract().path("user.name");
        assertEquals("User name doesn't match", register.getName(), name);
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Проверка входа с невалидными данными")
    public void postLoginInValidPasswordReturnsFault() {

        userCredentials.setPassword("111");

        step("Авторизоваться под несуществующим пользователем");
        Response response = burgersClient.login(userCredentials);

        step("Проверить, что статус ответа 401");
        int statusCode = response.then().extract().statusCode();
        assertEquals("Status code is not 401 UNAUTHORIZED", SC_UNAUTHORIZED, statusCode);

        step("Проверить, что в поле success false");
        boolean hasSuccess = response.then().extract().path("success");
        assertFalse(hasSuccess);

        step("Проверить поле message");
        String message = response.then().extract().path("message");
        String expected = "email or password are incorrect";
        assertEquals("Error message doesn't match", expected, message);
    }

    @Test
    @DisplayName("Логин с неверным логином")
    @Description("Проверка входа с невалидными данными")
    public void postLoginInValidEmailReturnsFault() {

        userCredentials.setEmail("ninja_turtle@example.com");

        step("Авторизоваться под несуществующим пользователем");
        Response response = burgersClient.login(userCredentials);

        step("Проверить, что статус ответа 401");
        int statusCode = response.then().extract().statusCode();
        assertEquals("Status code is not 401 UNAUTHORIZED", SC_UNAUTHORIZED, statusCode);

        step("Проверить, что в поле success false");
        boolean hasSuccess = response.then().extract().path("success");
        assertFalse(hasSuccess);

        step("Проверить поле message");
        String message = response.then().extract().path("message");
        String expected = "email or password are incorrect";
        assertEquals("Error message doesn't match", expected, message);
    }


    @After
    public void tearDown() {
        if (accessToken != null) {
            burgersClient.deleteUser(accessToken);
        }
    }
}
