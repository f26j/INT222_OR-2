package sit.int221.BEOR2.db2.dtos;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class LoginRequest {

    @NotEmpty(message = "Username cannot be empty")
    @Size(max = 50, message = "Username cannot be longer than 50 characters")
    private String username;

    @NotEmpty(message = "Password cannot be empty")
    @Size(max = 14, message = "Password cannot be longer than 14 characters")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}