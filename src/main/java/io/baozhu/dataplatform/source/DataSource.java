package io.baozhu.dataplatform.source;

import java.time.LocalDateTime;

public record DataSource(Long id, String name, String type, String connectionConfig, String status,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {}
