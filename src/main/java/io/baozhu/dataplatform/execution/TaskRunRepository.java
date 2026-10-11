package io.baozhu.dataplatform.execution;

import io.baozhu.dataplatform.common.ResourceNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

@Repository
public class TaskRunRepository {
    private final JdbcTemplate jdbc;
    public TaskRunRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public TaskRun create(String runKey, Long taskId) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var ps = connection.prepareStatement("INSERT INTO task_run(run_key,task_id,status) VALUES (?,?,?)", java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, runKey); ps.setLong(2, taskId); ps.setString(3, "RUNNING"); return ps;
        }, keys);
        return findById(((Number) keys.getKeys().get("ID")).longValue());
    }

    public TaskRun findById(Long id) {
        return jdbc.query("SELECT * FROM task_run WHERE id=?", this::map, id).stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Task run not found: " + id));
    }

    public TaskRun findByKey(String key) {
        return jdbc.query("SELECT * FROM task_run WHERE run_key=?", this::map, key).stream().findFirst().orElse(null);
    }

    public List<TaskRun> findByTask(Long taskId) { return jdbc.query("SELECT * FROM task_run WHERE task_id=? ORDER BY id DESC", this::map, taskId); }

    public TaskRun complete(Long id, long read, long written) {
        jdbc.update("UPDATE task_run SET status='SUCCESS',records_read=?,records_written=?,finished_at=CURRENT_TIMESTAMP WHERE id=?", read, written, id);
        return findById(id);
    }

    public TaskRun fail(Long id, String message) {
        jdbc.update("UPDATE task_run SET status='FAILED',error_message=?,finished_at=CURRENT_TIMESTAMP WHERE id=?", message, id);
        return findById(id);
    }

    private TaskRun map(ResultSet rs, int row) throws java.sql.SQLException {
        return new TaskRun(rs.getLong("id"), rs.getString("run_key"), rs.getLong("task_id"), rs.getString("status"),
                rs.getLong("records_read"), rs.getLong("records_written"), rs.getString("error_message"),
                rs.getTimestamp("started_at").toLocalDateTime(), rs.getTimestamp("finished_at") == null ? null : rs.getTimestamp("finished_at").toLocalDateTime());
    }
}
