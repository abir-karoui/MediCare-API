package tn.exemple.medicare.controllers.authcontrollers;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tn.exemple.medicare.configs.JwtService;
import tn.exemple.medicare.configs.UserDetailsServices;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.enums.TypeRole;
import tn.exemple.medicare.services.IUserSevices;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private IUserSevices iUserSevices;
    @Autowired
    private UserDetailsServices userDetailsServices;

    @PostMapping("/adduser")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> singUp(@RequestBody @Valid User u) throws MessagingException {
        iUserSevices.singUp(u);
        return  ResponseEntity.accepted().build();
    }
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request) {
        return ResponseEntity.ok( iUserSevices.login(request));
    }
    @GetMapping("/activate-account")
    public  void confirm(@RequestParam String code) throws MessagingException {
        iUserSevices.activateAccount(code);
    }
    @PatchMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request , Principal connectedUser) {
        iUserSevices.changePassword(request , connectedUser);
        return  ResponseEntity.accepted().build();
         }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request , HttpServletResponse response) throws IOException {
   iUserSevices.refreshToken(request , response) ;
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        iUserSevices.requestPasswordReset(email);
        return ResponseEntity.ok("A password reset link has been sent to your email");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        iUserSevices.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password successfully reset.");
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal User user) {

        iUserSevices.logout(user);
        return ResponseEntity.ok("Logout successful");
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

