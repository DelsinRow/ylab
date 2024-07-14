package com.sinaev.aspects;

import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.entities.AuditLog;
import com.sinaev.repositories.AuditLogRepository;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {
    private final AuditLogRepository auditLogRepository;

    @Pointcut("execution(* com.sinaev.controllers.*.*(..)) && args(req,..)")
    public void controllerMethods(HttpServletRequest req) {
    }

    @After("controllerMethods(req)")
    public void logAfter(JoinPoint joinPoint, HttpServletRequest req) {
        String username = getCurrentUsername(req);
        String action = joinPoint.getSignature().getName();
        LocalDateTime timestamp = LocalDateTime.now();

        AuditLog auditLog = new AuditLog(username, action, timestamp);
        if (auditLogRepository != null) {
            auditLogRepository.save(auditLog);
        }
    }

    private String getCurrentUsername(HttpServletRequest req) {
        UserDTO userDTO = (UserDTO) req.getSession().getAttribute("loggedIn");
        if (userDTO == null) return "unknown_user";
        else return userDTO.username();
    }
}
