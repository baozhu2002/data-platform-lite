package io.baozhu.dataplatform.quality;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateQualityRuleRequest(@NotBlank @Size(max=120) String name,
                                       @NotBlank @Size(max=120) String tableName,
                                       @NotBlank String ruleType,
                                       @NotBlank @Size(max=500) String expression,
                                       String severity) {}
