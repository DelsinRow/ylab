package com.sinaev.aspects;

import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.entities.AuditLog;
import com.sinaev.repositories.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.time.LocalDateTime;

@Aspect
public class AuditAspect {
    private static AuditLogRepository auditLogRepository;

    public AuditAspect() {
    }

    public static void init(AuditLogRepository repository) {
        auditLogRepository = repository;
    }

    @Pointcut("execution(* com.sinaev.servlets.*.*(..)) && args(req,..)")
    public void servletMethods(HttpServletRequest req) {
    }

    @After("servletMethods(req)")
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
