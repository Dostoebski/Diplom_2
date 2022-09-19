package model;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import util.StellarBurgersClient;

import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@Getter
@Setter
public class Order {

    private List<String> ingredients;

    public static Order getDefaultOrder(StellarBurgersClient client) {

        Response response = client.getIngredients();

        List<String> ingredients = new ArrayList<>();

        ingredients.add(response.then().extract().path("data._id[0]"));
        ingredients.add(response.then().extract().path("data._id[1]"));

        return new Order(ingredients);
    }
}
