package com.qudini.reactive.metrics.build;

import com.qudini.reactive.utils.metadata.MetadataService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class BuildInfoMeterBinder implements MeterBinder {

    private final MetadataService metadataService;

    private final String gaugeNamePrefix;

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge
                .builder(gaugeNamePrefix + "_build_info", () -> 1)
                .tag("name", metadataService.getBuildName())
                .tag("version", metadataService.getBuildVersion())
                .register(registry);
    }

}
