package com.sinaev.aspects;

import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.entities.AuditLog;
import com.sinaev.repositories.AuditLogRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Aspect
@Component
public class AuditAspect {
    private final AuditLogRepository auditLogRepository;

    public AuditAspect(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Pointcut("execution(* com.sinaev.controllers.*.*(..)) && args(javax.servlet.http.HttpServletRequest,..)")
    public void controllerMethods() {
    }

    @After("controllerMethods()")
    public void logAfter(JoinPoint joinPoint) {
        System.out.println("!!! I AM HERE !!!!!");
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

    private String getCurrentUsername(HttpServletRequest req) {
        UserDTO userDTO = (UserDTO) req.getSession().getAttribute("loggedIn");
        if (userDTO == null) return "unknown_user";
        else return userDTO.username();
    }
}