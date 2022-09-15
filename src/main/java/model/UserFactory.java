package model;

public class UserFactory {

    public static User getUser(Register register) {
        return new User(register.getEmail(), register.getName());
    }
}
