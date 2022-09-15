package util;

import io.restassured.response.Response;
import model.Order;
import model.Register;
import model.User;
import model.UserCredentials;

import static io.restassured.RestAssured.*;

public class StellarBurgersClient extends RestClient {

    private final String REGISTER_PATH = "/api/auth/register",
                         USER_PATH = "/api/auth/user",
                         ORDERS_PATH = "/api/orders",
                         INGREDIENTS_PATH = "/api/ingredients",
                         LOGIN_PATH = "/api/auth/login";

    public Response createUser(Register register) {
        return given()
                .spec(getBaseSpec())
                .body(register)
                .post(REGISTER_PATH);
    }

    public Response changeUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .patch(USER_PATH);
    }

    public Response changeUser(User user, String accessToken) {
        return given()
                .spec(getSpecWithAuthorization(accessToken))
                .body(user)
                .patch(USER_PATH);
    }

    public Response deleteUser(String accessToken) {
        return given()
                .spec(getSpecWithAuthorization(accessToken))
                .delete(USER_PATH);
    }

    public Response login(UserCredentials userCredentials) {
        return given()
                .spec(getBaseSpec())
                .body(userCredentials)
                .post(LOGIN_PATH);
    }

    public Response getIngredients() {
        return given()
                .spec(getBaseSpec())
                .get(INGREDIENTS_PATH);
    }

    public Response createOrder(Order order) {
        return given()
                .spec(getBaseSpec())
                .body(order)
                .post(ORDERS_PATH);
    }

    public Response createOrder(Order order, String accessToken) {
        return given()
                .spec(getSpecWithAuthorization(accessToken))
                .body(order)
                .post(ORDERS_PATH);
    }

    public Response getOrders() {
        return given()
                .spec(getBaseSpec())
                .get(ORDERS_PATH);
    }

    public Response getOrders(String accessToken) {
        return given()
                .spec(getSpecWithAuthorization(accessToken))
                .get(ORDERS_PATH);
    }
}
