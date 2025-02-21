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
    public  ResponseEntity<List<User>> retrieveAllUsers() {
        List<User> users = iUserSevices.retrieveAllUsers();
        if (users.isEmpty()) {
            throw new EntityNotFoundException("No users found in the database.");
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        User foundUser = iUserSevices.getUserById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found"));

        return ResponseEntity.ok(foundUser);
    }
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable("role") TypeRole role) {
        List<User> users = iUserSevices.getUsersByRole(role);
        if (users.isEmpty()) {
            throw new EntityNotFoundException("No users found with role: " + role);
        }
        return ResponseEntity.ok(users);
    }
    @PutMapping("{id}")
    User  UpdateUser(@PathVariable Long id, @RequestBody User user) {
        return iUserSevices.UpdateUser(id, user);
    }
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        try {
            iUserSevices.deleteUserById(id);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        }
    }
    @DeleteMapping("/")
    public void deleteAllUser(){ iUserSevices.deleteAllUser();}
}

