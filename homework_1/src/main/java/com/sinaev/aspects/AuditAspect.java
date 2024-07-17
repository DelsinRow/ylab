package com.sinaev.aspects;

import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.entities.AuditLog;
import com.sinaev.repositories.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * Aspect for auditing actions performed in the application.
 * <p>
 * This aspect intercepts methods annotated for auditing and logs the actions
 * performed by users along with a timestamp.
 * </p>
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {
    private final AuditLogRepository auditLogRepository;

    /**
     * Logs the action performed by a user after the execution of audited methods.
     * <p>
     * This advice runs after the execution of methods annotated for auditing,
     * retrieving the current user and logging their action along with a timestamp.
     * </p>
     *
     * @param joinPoint the join point representing the method execution
     */
    @After("Pointcuts.auditMethods()")
    public void logAfter(JoinPoint joinPoint) {
        HttpServletRequest req = null;
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof HttpServletRequest) {
                req = (HttpServletRequest) arg;
                break;
            }
        }

        if (req != null) {
            String username = getCurrentUsername(req);
            String action = joinPoint.getSignature().getName();
            LocalDateTime timestamp = LocalDateTime.now();

            AuditLog auditLog = new AuditLog(username, action, timestamp);
            if (auditLogRepository != null) {
                auditLogRepository.save(auditLog);
            }
        }
    }

    /**
     * Retrieves the current username from the HTTP request session.
     *
     * @param req the HTTP request containing the session
     * @return the username of the logged-in user, or "unknown_user" if not found
     */
    private String getCurrentUsername(HttpServletRequest req) {
        UserDTO userDTO = (UserDTO) req.getSession().getAttribute("loggedIn");
        if (userDTO == null) {
            return "unknown_user";
        } else {
            return userDTO.username();
        }
    }
}