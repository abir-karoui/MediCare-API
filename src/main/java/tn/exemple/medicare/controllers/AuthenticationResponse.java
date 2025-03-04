package tn.exemple.medicare.controllers;


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