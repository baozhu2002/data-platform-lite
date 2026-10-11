package io.baozhu.dataplatform.execution;

import java.time.LocalDateTime;

public record TaskRun(Long id, String runKey, Long taskId, String status, Long recordsRead,
                      Long recordsWritten, String errorMessage, LocalDateTime startedAt,
                      LocalDateTime finishedAt) {}
