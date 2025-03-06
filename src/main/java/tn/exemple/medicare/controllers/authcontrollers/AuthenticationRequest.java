package tn.exemple.medicare.controllers.authcontrollers;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Data
@Getter
@Setter
public class AuthenticationRequest {
    @Email( message = "Email is not formated")
    @NotEmpty( message = "Email is not formated")
    @NotBlank(message = "Email is not formated")
    private String email;
    @NotEmpty ( message = "First is mandatory")
    @NotBlank (message = "First is mandatory")
    @Size(min =8 , message = "Password should be 8 characters long minimum")
    private String password;


}
