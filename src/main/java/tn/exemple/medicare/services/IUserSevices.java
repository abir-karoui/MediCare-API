package tn.exemple.medicare.services;
import tn.exemple.medicare.controllers.AuthenticationRequest;
import tn.exemple.medicare.controllers.AuthenticationResponse;
import tn.exemple.medicare.entities.User;
import tn.exemple.medicare.enums.TypeRole;
import java.util.List;
import java.util.Optional;

public interface IUserSevices {

    User addUser(User user);
    List<User> retrieveAllUsers();
    Optional<User> getUserById(Long id);
    List<User> getUsersByRole(TypeRole role);
    User UpdateUser(Long id , User user) ;
    void  deleteUserById(Long id);
    void deleteAllUser();
    AuthenticationResponse login(AuthenticationRequest request);

    // void sendValidationEmail(User user);
    /*void activateAccount(String token);*/
}
