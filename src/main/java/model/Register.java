package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;

@AllArgsConstructor
@Getter
@Setter
public class Register {

    private String email;
    private String password;
    private String name;

    public static Register getDefaultRegister() {
        Random random = new Random();
        String randomEmail = random.nextInt(10000) + "data-test@example.com";
        return new Register(
                randomEmail,
                "praktikum",
                "Josh"
        );
    }
}
