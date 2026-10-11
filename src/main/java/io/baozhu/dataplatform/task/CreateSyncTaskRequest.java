package io.baozhu.dataplatform.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateSyncTaskRequest(
        @NotBlank @Size(max = 120) String name,
        @NotNull Long sourceId,
        @NotBlank @Size(max = 120) String targetTable,
        @NotBlank String syncMode,
        String scheduleExpression) {}
