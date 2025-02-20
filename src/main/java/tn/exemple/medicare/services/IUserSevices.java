package tn.exemple.medicare.services;
import tn.exemple.medicare.entities.User;
import tn.exemple.medicare.enums.TypeRole;
import java.util.List;
import java.util.Optional;

public interface IUserSevices {
    User addUser(User user);
    List<User> retrieveAllUsers();
    //User getUserById(Long id) throws Exception ;
   User getUserById(Long id) throws Exception ;
    List<User> getUsersByRole(TypeRole role);
    User UpdateUser(Long id , User user) ;
    void  deleteUserById(Long id);
    void deleteAllUser();

}
