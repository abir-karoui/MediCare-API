package tn.exemple.medicare.controllers;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ChangePasswordRequest {

    private  String currentPassword;
    private  String newPassword;
    private  String confirmationPassword;

}
