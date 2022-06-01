package com.qudini.reactive.metrics.security;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProbesPaths {

    @Builder.Default
    String liveness = "/liveness";

    @Builder.Default
    String readiness = "/readiness";

    @Builder.Default
    String metrics = "/metrics";

}
