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
import org.springframework.web.multipart.MultipartFile;
import tn.exemple.medicare.configs.JwtService;
import tn.exemple.medicare.configs.UserDetailsServices;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.dto.UserDto;
import tn.exemple.medicare.enums.TypeRole;
import tn.exemple.medicare.services.IUserSevices;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
    public record SignUpMessage(AuthenticationResponse user , String message) {}

    @PostMapping("/adduser")

    public ResponseEntity<?> addUser(
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam(value = "medicalCard", required = false) MultipartFile medicalCard,
            @RequestParam @Valid Map<String, Object> userMap

    ) throws Exception {
        AuthenticationResponse user = iUserSevices.register(userMap , photo,medicalCard);
        SignUpMessage message = new SignUpMessage(user, "An activation code has been sent to your email");
        return ResponseEntity.accepted().body(message);
    }




    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationRequest request) {
        return ResponseEntity.ok( iUserSevices.login(request));
    }
    @GetMapping("/activate-account")
    public  ResponseEntity<?> confirm(@RequestParam String code) throws MessagingException {
        iUserSevices.activateAccount(code);
        return ResponseEntity.ok("Account successfully activated");
    }
    @PatchMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request , Principal connectedUser) {
        iUserSevices.changePassword(request , connectedUser);
        return  ResponseEntity.ok("Password successfully changed");
         }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request , HttpServletResponse response) throws IOException {
    iUserSevices.refreshToken(request , response) ;
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) throws MessagingException {
        iUserSevices.requestPasswordReset(email);
        return ResponseEntity.ok("A code to reset your password has been sent to your email");
    }
    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyResetCode(@RequestParam String email, @RequestParam String code) {
        boolean isValid = iUserSevices.verifyResetCode(email, code);
        return isValid ? ResponseEntity.ok("Code is valid") : ResponseEntity.badRequest().body("Invalid code");
    }
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String email,
            @RequestParam String code,
            @RequestParam String newPassword) {
        iUserSevices.resetPassword(email, code, newPassword);
        return ResponseEntity.ok("Password successfully reset.");
    }


    /*@PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String code,
            @RequestParam String newPassword) {
        iUserSevices.resetPassword(code, newPassword);
        return ResponseEntity.ok("Password successfully reset.");
    }*/


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
    @GetMapping("/list/role")
    public ResponseEntity<List<UserDto>> getUsersByRole() {
        List<UserDto> users = iUserSevices.getUsersByRole();
        return ResponseEntity.ok(users);
    }

    @PutMapping("{id}")
    User  UpdateUser(@PathVariable Long id, @RequestParam User user) {
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

