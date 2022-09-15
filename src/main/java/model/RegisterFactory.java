package model;

public class RegisterFactory {
    public static Register getDefaultRegister() {
        return new Register(
                "data-test@example.com",
                "praktikum",
                "Josh"
        );
    }
}
