import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class OrderCreationTest extends BaseTest {

    private String accessToken;
    private Order order;

    @Before
    public void setUp() {
        Register register = RegisterFactory.getDefaultRegister();
        accessToken = burgersClient.createUser(register).then().extract().path("accessToken");
        order = OrderFactory.getDefaultOrder(burgersClient);
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Проверка создания заказа")
    public void postOrdersTwoIngredientsReturnsSuccess() {

        step("Создать заказ");
        Response response = burgersClient.createOrder(order, accessToken);

        step("Проверить, что статус ответа 200");
        int statusCode = response.then().extract().statusCode();
        assertEquals("Status code is not 200 OK", SC_OK, statusCode);

        step("Проверить, что в поле success true");
        boolean hasSuccess = response.then().extract().path("success");
        assertTrue(hasSuccess);

        step("Проверить, что order number сформировался");
        Integer orderNumber = response.then().extract().path("order.number");
        assertNotNull(orderNumber);

        step("Проверить, что название сформировалось");
        String name = response.then().extract().path("name");
        assertNotNull(name);
    }

    @Test
    @DisplayName("Неавторизованное создание заказа")
    @Description("Проверка создания заказа неавторизованным пользователем")
    public void postOrdersUnauthorizedReturnsFault() {

        step("Создать заказ");
        Response response = burgersClient.createOrder(order);

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

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка создания заказа с пустым списком ингредиентов")
    public void postOrdersWithoutIngredientsReturnsFault() {

        order.setIngredients(List.of());

        step("Создать заказ");
        Response response = burgersClient.createOrder(order, accessToken);

        step("Проверить, что статус ответа 400");
        int statusCode = response.then().extract().statusCode();
        assertEquals("Status code is not 400 BAD REQUEST", SC_BAD_REQUEST, statusCode);

        step("Проверить, что в поле success false");
        boolean hasSuccess = response.then().extract().path("success");
        assertFalse(hasSuccess);

        step("Проверить поле message");
        String message = response.then().extract().path("message");
        String expected = "Ingredient ids must be provided";
        assertEquals("Error message doesn't match", expected, message);
    }

    @Test
    @DisplayName("Создание заказа c неверным хэшем ингредиентов")
    @Description("Проверка создания заказа c неверными id ингредиентов")
    public void postOrdersWrongIngredientsHashReturnsFault() {

        order.setIngredients(List.of("11", "55"));

        step("Создать заказ");
        Response response = burgersClient.createOrder(order, accessToken);

        step("Проверить, что статус ответа 500");
        int statusCode = response.then().extract().statusCode();
        assertEquals("Status code is not 500 INTERNAL SERVER ERROR", SC_INTERNAL_SERVER_ERROR, statusCode);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            burgersClient.deleteUser(accessToken);
        }
    }
}
