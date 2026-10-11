package io.baozhu.dataplatform.audit;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuditEventRepository {
    private final JdbcTemplate jdbc;
    public AuditEventRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    public void append(String type, String resourceType, String resourceId, String payload) {
        jdbc.update("INSERT INTO audit_event(event_type,resource_type,resource_id,payload) VALUES (?,?,?,?)",
                type, resourceType, resourceId, payload);
    }
}
