package tn.exemple.medicare.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.entities.User;
import tn.exemple.medicare.repositories.IUserRepository;

import java.util.Collection;
import java.util.Collections;


@Service

public class UserDetailsServices  implements UserDetailsService {
    private final IUserRepository iUserRepository;

    public UserDetailsServices(IUserRepository iUserRepository) {
        this.iUserRepository = iUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user =iUserRepository.findByEmail(email);
        String role = "ROLE_" + user.getRole().name();
        if (user == null ){
            throw  new UsernameNotFoundException("User not found with email :" +email);
        }
        return  new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(role)));
    }
}
