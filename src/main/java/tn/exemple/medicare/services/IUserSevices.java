package tn.exemple.medicare.services;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tn.exemple.medicare.controllers.authcontrollers.AuthenticationRequest;
import tn.exemple.medicare.controllers.authcontrollers.AuthenticationResponse;
import tn.exemple.medicare.controllers.authcontrollers.ChangePasswordRequest;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.enums.TypeCode;
import tn.exemple.medicare.enums.TypeRole;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface IUserSevices {

    AuthenticationResponse  singUp(User user) throws MessagingException;
    AuthenticationResponse login(AuthenticationRequest request);

    void changePassword(ChangePasswordRequest request , Principal connectedUser);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

    void activateAccount(String token) throws MessagingException;

    void sendValidationEmail(User user) throws MessagingException;

    void requestPasswordReset(String email);
    void sendPasswordResetEmail(String email, String token);
    void resetPassword(String token, String newPassword);

    List<User> retrieveAllUsers();
    Optional<User> getUserById(Long id);
    List<User> getUsersByRole(TypeRole role);
    User UpdateUser(Long id , User user) ;
    void  deleteUserById(Long id);
    void deleteAllUser();
    void logout(User user);


}
