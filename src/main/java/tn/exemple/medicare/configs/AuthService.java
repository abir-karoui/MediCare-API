package tn.exemple.medicare.configs;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.enums.TypeRole;

@Service
public class AuthService {
    public Long getAuthenticatedUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }

    public TypeRole getAuthenticatedUserRole() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getRole();
    }
}

