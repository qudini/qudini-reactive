package com.qudini.reactive.metrics.buildinfo;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class DefaultBuildInfoMeterBinder implements BuildInfoMeterBinder {

    private final BuildInfoService buildInfoService;

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge
                .builder("build_info", () -> 1)
                .tag("name", buildInfoService.getName())
                .tag("version", buildInfoService.getVersion())
                .register(registry);
    }

}
