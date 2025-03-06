package tn.exemple.medicare.configs;

import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.repositories.IUserRepository;


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
