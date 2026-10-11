package io.baozhu.dataplatform.task;

import jakarta.validation.Valid;
import io.baozhu.dataplatform.execution.TaskExecutionService;
import io.baozhu.dataplatform.execution.TaskRun;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sync-tasks")
public class SyncTaskController {
    private final SyncTaskRepository repository;
    private final TaskExecutionService executionService;
    public SyncTaskController(SyncTaskRepository repository, TaskExecutionService executionService) { this.repository = repository; this.executionService = executionService; }

    @GetMapping
    public List<SyncTask> list() { return repository.findAll(); }

    @GetMapping("/{id}")
    public SyncTask get(@PathVariable Long id) { return repository.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SyncTask create(@Valid @RequestBody CreateSyncTaskRequest request) { return repository.save(request); }

    @PostMapping("/{id}/run")
    public TaskRun run(@PathVariable Long id, @RequestHeader(value = "Idempotency-Key", required = false) String key) { return executionService.execute(id, key); }
}
