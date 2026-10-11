package io.baozhu.dataplatform.quality;

import io.baozhu.dataplatform.common.ResourceNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

@Repository
public class QualityRuleRepository {
    private final JdbcTemplate jdbc;
    public QualityRuleRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    public QualityRule save(CreateQualityRuleRequest r) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.update(c -> { var ps=c.prepareStatement("INSERT INTO quality_rule(name,table_name,rule_type,expression,severity) VALUES (?,?,?,?,?)", java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,r.name()); ps.setString(2,r.tableName()); ps.setString(3,r.ruleType().toUpperCase()); ps.setString(4,r.expression()); ps.setString(5,r.severity()==null?"ERROR":r.severity().toUpperCase()); return ps; }, keys);
        return findById(((Number) keys.getKeys().get("ID")).longValue());
    }
    public List<QualityRule> findAll() { return jdbc.query("SELECT * FROM quality_rule ORDER BY id DESC", this::map); }
    public QualityRule findById(Long id) { return jdbc.query("SELECT * FROM quality_rule WHERE id=?", this::map, id).stream().findFirst().orElseThrow(() -> new ResourceNotFoundException("Quality rule not found: " + id)); }
    private QualityRule map(ResultSet rs,int row) throws java.sql.SQLException { return new QualityRule(rs.getLong("id"),rs.getString("name"),rs.getString("table_name"),rs.getString("rule_type"),rs.getString("expression"),rs.getString("severity"),rs.getBoolean("enabled"),rs.getTimestamp("created_at").toLocalDateTime()); }
}
