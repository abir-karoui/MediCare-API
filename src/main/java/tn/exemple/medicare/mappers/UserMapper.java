package tn.exemple.medicare.mappers;

import org.springframework.stereotype.Component;
import tn.exemple.medicare.entities.Doctor;
import tn.exemple.medicare.entities.Patient;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.dto.UserDto;

@Component
public class UserMapper {

    public static UserDto toDto(User user) {
        if (user == null) return null;

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setPhoto(user.getPhoto());
        dto.setAddress(user.getAddress());
        dto.setRole(user.getRole());
        dto.setGender(user.getGender());
        if (user instanceof Doctor doctor) {
            if (doctor.getSpeciality() != null) {
                dto.setSpeciality(doctor.getSpeciality());
            }
        }

        if (user instanceof Patient patient) {
            if (patient.getAge() != null) {
                dto.setAge(patient.getAge());
            }
        }
        return dto;
    }

}
