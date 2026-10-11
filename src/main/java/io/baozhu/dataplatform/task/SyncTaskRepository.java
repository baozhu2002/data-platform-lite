package io.baozhu.dataplatform.task;

import io.baozhu.dataplatform.common.ResourceNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

@Repository
public class SyncTaskRepository {
    private final JdbcTemplate jdbc;
    public SyncTaskRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public SyncTask save(CreateSyncTaskRequest request) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var statement = connection.prepareStatement(
                    "INSERT INTO sync_task(name,source_id,target_table,sync_mode,schedule_expression) VALUES (?,?,?,?,?)",
                    java.sql.Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, request.name());
            statement.setLong(2, request.sourceId());
            statement.setString(3, request.targetTable());
            statement.setString(4, request.syncMode().toUpperCase());
            statement.setString(5, request.scheduleExpression());
            return statement;
        }, keys);
        return findById(((Number) keys.getKeys().get("ID")).longValue());
    }

    public List<SyncTask> findAll() { return jdbc.query("SELECT * FROM sync_task ORDER BY id DESC", this::map); }

    public SyncTask findById(Long id) {
        return jdbc.query("SELECT * FROM sync_task WHERE id = ?", this::map, id).stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Sync task not found: " + id));
    }

    public SyncTask markRun(Long id) {
        findById(id);
        jdbc.update("UPDATE sync_task SET status='SUCCESS', retry_count=0, last_error=NULL, updated_at=CURRENT_TIMESTAMP WHERE id=?", id);
        return findById(id);
    }

    private SyncTask map(ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new SyncTask(rs.getLong("id"), rs.getString("name"), rs.getLong("source_id"),
                rs.getString("target_table"), rs.getString("sync_mode"), rs.getString("schedule_expression"),
                rs.getString("status"), rs.getInt("retry_count"), rs.getString("last_error"),
                rs.getTimestamp("created_at").toLocalDateTime(), rs.getTimestamp("updated_at").toLocalDateTime());
    }
}

