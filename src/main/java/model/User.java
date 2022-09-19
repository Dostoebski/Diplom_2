package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class User {
    private String email;
    private String name;

    public static User getUser(Register register) {
        return new User(register.getEmail(), register.getName());
    }
}
