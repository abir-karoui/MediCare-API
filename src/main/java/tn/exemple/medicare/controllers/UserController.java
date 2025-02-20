package tn.exemple.medicare.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.exemple.medicare.entities.User;
import tn.exemple.medicare.enums.TypeRole;
import tn.exemple.medicare.exceptions.UserException;
import tn.exemple.medicare.services.IUserSevices;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private IUserSevices iUserSevices;

    @PostMapping("/adduser")
    User addUser(@RequestBody User u) {
        return iUserSevices.addUser(u);
    }

    @GetMapping("/all")
    List<User> retrieveAllUsers() {
        return iUserSevices.retrieveAllUsers();
    }
    @GetMapping("{id}")
    public User getUserById(@PathVariable("id") Long id) throws Exception{
        return  iUserSevices.getUserById(id);
    }

    @GetMapping("/role/{role}")
    List<User> getUsersByRole(@PathVariable("role") TypeRole role) {
        return iUserSevices.getUsersByRole(role);
    }

    @PutMapping("{id}")
    User  UpdateUser(@PathVariable Long id, @RequestBody User user) {
        return iUserSevices.UpdateUser(id, user);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {

        try{
            iUserSevices.deleteUserById(id);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);

        }   catch (EntityNotFoundException e) {
        return new ResponseEntity<>( e.getMessage(), HttpStatus.NOT_FOUND);
    }
       }
    @DeleteMapping("/")
    public void deleteAllUser(){ iUserSevices.deleteAllUser();}
}

