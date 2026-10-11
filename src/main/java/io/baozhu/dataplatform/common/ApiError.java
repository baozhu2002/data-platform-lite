package io.baozhu.dataplatform.common;

import java.time.Instant;

public record ApiError(Instant timestamp, int status, String error, String message, String path) {}
