package io.baozhu.dataplatform.quality;

public record QualitySummary(long rules, long passed, long failed, double passRate) {}
