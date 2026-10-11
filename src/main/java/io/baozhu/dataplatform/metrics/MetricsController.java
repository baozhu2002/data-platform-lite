package io.baozhu.dataplatform.metrics;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/metrics")
public class MetricsController {
    private final JdbcTemplate jdbc;
    public MetricsController(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    @GetMapping("/overview")
    @Cacheable("platform-metrics")
    public Map<String,Object> overview() {
        long total = jdbc.queryForObject("SELECT COUNT(*) FROM task_run", Long.class);
        long success = jdbc.queryForObject("SELECT COUNT(*) FROM task_run WHERE status='SUCCESS'", Long.class);
        long rows = jdbc.queryForObject("SELECT COALESCE(SUM(records_written),0) FROM task_run WHERE status='SUCCESS'", Long.class);
        double rate = total == 0 ? 1.0 : ((double) success / total);
        return Map.of("pipelineSuccessRate", rate, "recordsProcessed", rows, "totalRuns", total, "successfulRuns", success);
    }
}
