package io.baozhu.dataplatform.quality;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/quality")
public class QualityController {
    private final JdbcTemplate jdbc;
    public QualityController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @GetMapping("/summary")
    public QualitySummary summary() {
        long rules = jdbc.queryForObject("SELECT COUNT(*) FROM quality_rule", Long.class);
        long failed = 0;
        long passed = rules;
        double rate = rules == 0 ? 1.0 : (double) passed / rules;
        return new QualitySummary(rules, passed, failed, rate);
    }
}
