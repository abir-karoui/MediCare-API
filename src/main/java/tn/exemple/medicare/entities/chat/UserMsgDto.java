package tn.exemple.medicare.entities.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.exemple.medicare.enums.TypeRole;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserMsgDto {
    private long id;
    private String firstname;
    private String lastname;
    private String email;
    private TypeRole role;
}
