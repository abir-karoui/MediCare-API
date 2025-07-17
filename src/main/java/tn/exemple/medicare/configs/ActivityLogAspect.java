package tn.exemple.medicare.configs;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.services.IActivityLogService;

@Aspect
@Component
public class ActivityLogAspect {

    @Autowired
    private IActivityLogService activityLogService;

    @Autowired
    private HttpServletRequest request;

    @Around("@annotation(loggableAction)")
    public Object logActivity(ProceedingJoinPoint joinPoint, LoggableAction loggableAction) throws Throwable {
        Object result = joinPoint.proceed(); // Exécution de la méthode

        String title = loggableAction.title();
        String description = loggableAction.description();
        String performedBy = getCurrentUser();

        activityLogService.logActivity(title, performedBy, description);

        return result;
    }

    private String getCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getFirstname() + " " + user.getLastname();
    }
}

