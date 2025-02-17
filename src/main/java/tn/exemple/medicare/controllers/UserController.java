package tn.exemple.medicare.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.exemple.medicare.entities.Doctor;
import tn.exemple.medicare.entities.Patient;
import tn.exemple.medicare.entities.TypeRole;
import tn.exemple.medicare.entities.User;
import tn.exemple.medicare.services.IUserSevices;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private IUserSevices iUserSevices ;
    @PostMapping("/adduser")
    User addUser(@RequestBody User u ){ return  iUserSevices.addUser(u) ;}

}
