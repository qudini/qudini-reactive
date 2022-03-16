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
public class DoubleInterval implements NumberInterval<Double, DoubleInterval> {

    Double start;
    Double end;

    @Override
    public @NonNull DoubleInterval withStart(Double start) {
        return new DoubleInterval(start, end);
    }

    @Override
    public @NonNull DoubleInterval withEnd(Double end) {
        return new DoubleInterval(start, end);
    }

}
