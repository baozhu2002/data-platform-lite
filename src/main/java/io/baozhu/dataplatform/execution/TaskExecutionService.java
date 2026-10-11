package io.baozhu.dataplatform.execution;

import io.baozhu.dataplatform.audit.AuditEventRepository;
import io.baozhu.dataplatform.common.ResourceNotFoundException;
import io.baozhu.dataplatform.task.SyncTask;
import io.baozhu.dataplatform.task.SyncTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TaskExecutionService {
    private final SyncTaskRepository tasks;
    private final TaskRunRepository runs;
    private final AuditEventRepository audit;

    public TaskExecutionService(SyncTaskRepository tasks, TaskRunRepository runs, AuditEventRepository audit) {
        this.tasks = tasks; this.runs = runs; this.audit = audit;
    }

    @Transactional
    public TaskRun execute(Long taskId, String idempotencyKey) {
        SyncTask task = tasks.findById(taskId);
        String key = idempotencyKey == null || idempotencyKey.isBlank() ? UUID.randomUUID().toString() : idempotencyKey;
        TaskRun existing = runs.findByKey(key);
        if (existing != null) return existing;
        TaskRun run = runs.create(key, task.id());
        try {
            // The adapter layer will replace this deterministic simulation with JDBC/CSV/OSS readers in the next iteration.
            long records = Math.max(1, task.targetTable().length() * 100L);
            TaskRun completed = runs.complete(run.id(), records, records);
            audit.append("TASK_RUN_SUCCEEDED", "SYNC_TASK", task.id().toString(), "{\"runId\":" + run.id() + "}");
            return completed;
        } catch (RuntimeException ex) {
            TaskRun failed = runs.fail(run.id(), ex.getMessage());
            audit.append("TASK_RUN_FAILED", "SYNC_TASK", task.id().toString(), ex.getMessage());
            return failed;
        }
    }

    public List<TaskRun> history(Long taskId) {
        tasks.findById(taskId);
        return runs.findByTask(taskId);
    }
}
