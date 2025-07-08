package com.gardengroup.agroplantationapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.gardengroup.agroplantationapp.model.entity.AuditLog;
import com.gardengroup.agroplantationapp.model.repository.AuditLogRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Component
public class AuditInterceptor implements HandlerInterceptor {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        AuditLog log = new AuditLog();
        log.setUsername(request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "ANÓNIMO");
        log.setAction("Acceso");
        log.setMethod(request.getMethod());
        log.setEndpoint(request.getRequestURI());
        log.setTimestamp(LocalDateTime.now());
        log.setIp(request.getRemoteAddr());

        auditLogRepository.save(log);
        return true;
    }
}