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
public class LongInterval implements NumberInterval<Long, LongInterval> {

    Long start;
    Long end;

    @Override
    public @NonNull LongInterval withStart(Long start) {
        return new LongInterval(start, end);
    }

    @Override
    public @NonNull LongInterval withEnd(Long end) {
        return new LongInterval(start, end);
    }

}
