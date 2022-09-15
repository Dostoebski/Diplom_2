package model;

import io.restassured.response.Response;
import util.StellarBurgersClient;

import java.util.ArrayList;
import java.util.List;

public class OrderFactory {

    public static Order getDefaultOrder(StellarBurgersClient client) {

        Response response = client.getIngredients();

        List<String> ingredients = new ArrayList<>();

        ingredients.add(response.then().extract().path("data._id[0]"));
        ingredients.add(response.then().extract().path("data._id[1]"));

        return new Order(ingredients);
    }
}
