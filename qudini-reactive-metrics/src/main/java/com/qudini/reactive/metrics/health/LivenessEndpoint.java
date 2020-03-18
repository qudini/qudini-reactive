package com.qudini.reactive.metrics.health;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.health.Health;

@Endpoint(id = "liveness")
public class LivenessEndpoint {

    @ReadOperation
    public Health check() {
        return Health.up().build();
    }

}
