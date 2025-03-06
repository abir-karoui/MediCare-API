package tn.exemple.medicare.controllers.authcontrollers;


import lombok.*;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;
}