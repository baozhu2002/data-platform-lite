package io.baozhu.dataplatform.task;

import java.time.LocalDateTime;

public record SyncTask(Long id, String name, Long sourceId, String targetTable, String syncMode,
                       String scheduleExpression, String status, Integer retryCount, String lastError,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {}
