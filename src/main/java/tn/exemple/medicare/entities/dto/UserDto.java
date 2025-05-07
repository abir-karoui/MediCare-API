package tn.exemple.medicare.entities.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.exemple.medicare.enums.TypeGender;
import tn.exemple.medicare.enums.TypeRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private long id;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String photo;
    private String address;
    private TypeRole role;
    private TypeGender gender ;
    private String speciality;
    private String age;

}
