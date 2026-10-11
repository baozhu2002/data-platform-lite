package io.baozhu.dataplatform.execution;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sync-tasks/{taskId}/runs")
public class TaskRunController {
    private final TaskExecutionService service;
    public TaskRunController(TaskExecutionService service) { this.service = service; }

    @PostMapping
    public TaskRun run(@PathVariable Long taskId, @RequestHeader(value = "Idempotency-Key", required = false) String key) {
        return service.execute(taskId, key);
    }

    @GetMapping
    public List<TaskRun> history(@PathVariable Long taskId) { return service.history(taskId); }
}
