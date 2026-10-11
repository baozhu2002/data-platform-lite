package io.baozhu.dataplatform.quality;

import java.time.LocalDateTime;

public record QualityRule(Long id, String name, String tableName, String ruleType, String expression,
                          String severity, Boolean enabled, LocalDateTime createdAt) {}
