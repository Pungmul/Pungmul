package pungmul.pungmul.core.exception.custom.member;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NoSuchUsernameException extends RuntimeException {
    private String username;

    public NoSuchUsernameException() {
        super();
    }

    public NoSuchUsernameException(String username) {
        super("User with username " + username + " not found");
        this.username = username;
    }

//    public String getUsername() {
//        return username;
//    }
}
