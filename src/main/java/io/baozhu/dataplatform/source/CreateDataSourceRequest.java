package io.baozhu.dataplatform.source;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateDataSourceRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 30) String type,
        @NotBlank @Size(max = 2000) String connectionConfig) {}
