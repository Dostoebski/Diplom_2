package model;

public class UserCredentialsFactory {
    public static UserCredentials getCredentials(Register register) {
        return new UserCredentials(register.getEmail(), register.getPassword());
    }
}
