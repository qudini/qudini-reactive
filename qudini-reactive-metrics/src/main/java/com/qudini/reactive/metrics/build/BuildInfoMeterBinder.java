package com.qudini.reactive.metrics.build;

import com.qudini.reactive.utils.build.BuildInfoService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class BuildInfoMeterBinder implements MeterBinder {

    private final BuildInfoService buildInfoService;

    private final String gaugeNamePrefix;

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge
                .builder(gaugeNamePrefix + "_build_info", () -> 1)
                .tag("name", buildInfoService.getName())
                .tag("version", buildInfoService.getVersion())
                .register(registry);
    }

}
