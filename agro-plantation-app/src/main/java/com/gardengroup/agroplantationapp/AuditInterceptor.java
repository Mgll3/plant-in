import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuditInterceptor extends HandlerInterceptorAdapter {

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