package tn.exemple.medicare.services;
import jakarta.mail.MessagingException;
import tn.exemple.medicare.controllers.AuthenticationRequest;
import tn.exemple.medicare.controllers.AuthenticationResponse;
import tn.exemple.medicare.controllers.ChangePasswordRequest;
import tn.exemple.medicare.entities.User;
import tn.exemple.medicare.enums.TypeRole;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface IUserSevices {

    User addUser(User user) throws MessagingException;
    List<User> retrieveAllUsers();
    Optional<User> getUserById(Long id);
    List<User> getUsersByRole(TypeRole role);
    User UpdateUser(Long id , User user) ;
    void  deleteUserById(Long id);
    void deleteAllUser();
    AuthenticationResponse login(AuthenticationRequest request);

    void changePassword(ChangePasswordRequest request , Principal connectedUser);

    // void sendValidationEmail(User user);
    /*void activateAccount(String token);*/
}
