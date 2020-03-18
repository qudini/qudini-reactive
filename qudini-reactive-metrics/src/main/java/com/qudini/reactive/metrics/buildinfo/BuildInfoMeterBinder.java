package com.qudini.reactive.metrics.buildinfo;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class BuildInfoMeterBinder implements MeterBinder {

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
