package com.qudini.reactive.metrics.health;

import org.springframework.boot.actuate.health.Health;

public interface LivenessEndpoint {

    Health check();

}
