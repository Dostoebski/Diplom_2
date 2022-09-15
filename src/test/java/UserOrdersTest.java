import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.*;

public class UserOrdersTest extends BaseTest {

    private static String accessToken;

    @Before
    public void setUp() {
        Register register = RegisterFactory.getDefaultRegister();
        accessToken = burgersClient.createUser(register).then().extract().path("accessToken");
        Order order = OrderFactory.getDefaultOrder(burgersClient);
        burgersClient.createOrder(order, accessToken);
    }

    @Test
    @DisplayName("Получения заказов пользователя")
    @Description("Проверка получения списка заказов пользователя")
    public void getOrdersReturnsSuccess() {

        step("Получить список");
        Response response = burgersClient.getOrders(accessToken);

        step("Проверить, что статус ответа 200");
        int statusCode = response.then().extract().statusCode();
        assertEquals("Status code is not 200 OK", SC_OK, statusCode);

        step("Проверить, что в поле success true");
        boolean hasSuccess = response.then().extract().path("success");
        assertTrue(hasSuccess);

        step("Проверить, что поле total не пустое");
        Integer total = response.then().extract().path("total");
        assertNotNull(total);

        step("Проверить, что поле totalToday не пустое");
        Integer totalToday = response.then().extract().path("totalToday");
        assertNotNull(totalToday);

        step("Проверить, что список содержит один заказ");
        int orderSize = response.then().extract().path("orders.size()");
        assertEquals("Must be one order in response", 1, orderSize);
    }

    @Test
    @DisplayName("Получения заказов неавторизованного пользователя")
    @Description("Проверка получения списка заказов неавторизованного пользователя")
    public void getOrdersUnauthorizedReturnsFault() {

        step("Получить список");
        Response response = burgersClient.getOrders();

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
