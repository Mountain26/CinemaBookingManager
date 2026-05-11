package finalproject_cinemabooking.model.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String password;
    private String confirmPassword;
    private String fullName;
    private String email;
    private String phone;
}
