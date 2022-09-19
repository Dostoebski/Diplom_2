package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserCredentials {

    private String email;
    private String password;

    public static UserCredentials getCredentials(Register register) {
        return new UserCredentials(register.getEmail(), register.getPassword());
    }
}
