package tn.exemple.medicare.services;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import tn.exemple.medicare.controllers.authcontrollers.AuthenticationRequest;
import tn.exemple.medicare.controllers.authcontrollers.AuthenticationResponse;
import tn.exemple.medicare.controllers.authcontrollers.ChangePasswordRequest;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.dto.UserDto;
import tn.exemple.medicare.enums.TypeCode;
import tn.exemple.medicare.enums.TypeRole;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IUserSevices {


    AuthenticationResponse register(Map<String, Object> userMap, MultipartFile photo, MultipartFile medicalCard) throws Exception;

    AuthenticationResponse login(AuthenticationRequest request);

    void changePassword(ChangePasswordRequest request , Principal connectedUser);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

    void activateAccount(String token) throws MessagingException;

    void sendValidationEmail(User user) throws MessagingException;

    void requestPasswordReset(String email) throws MessagingException;
    void sendResetEmail(User user , String code) throws MessagingException;
    //void resetPassword(String token, String newPassword);
    boolean verifyResetCode(String email, String code);
    void resetPassword(String email, String code, String newPassword);

    List<User> retrieveAllUsers();
    Optional<User> getUserById(Long id);
    List<UserDto> getUsersByRole();
    //User UpdateUser(Long id , User user) ;
    void  deleteUserById(Long id);
    void deleteAllUser();
    void logout(User user);
    UserDto getMe() ;
    void deleteMe(String password);
    AuthenticationResponse updateProfile(Map<String, Object> userParams, MultipartFile file)  throws Exception;


}
