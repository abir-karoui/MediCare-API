package tn.exemple.medicare.configs;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.entities.User;
import tn.exemple.medicare.repositories.IUserRepository;

import java.util.Collections;


@Service

public class UserDetailsServices  implements UserDetailsService {
    private final IUserRepository iUserRepository;
    public UserDetailsServices(IUserRepository iUserRepository) {
        this.iUserRepository = iUserRepository;
    }

    @Override
    @Transactional

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return iUserRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

}
