package com.qudini.reactive.utils.intervals.number;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@AllArgsConstructor(staticName = "of")
public class FloatInterval implements NumberInterval<Float, FloatInterval> {

    Float start;
    Float end;

    @Override
    public @NonNull FloatInterval withStart(Float start) {
        return new FloatInterval(start, end);
    }

    @Override
    public @NonNull FloatInterval withEnd(Float end) {
        return new FloatInterval(start, end);
    }

}
