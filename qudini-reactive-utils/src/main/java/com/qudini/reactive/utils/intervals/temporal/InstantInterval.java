package com.qudini.reactive.utils.intervals.temporal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Value
@Builder
@Jacksonized
@AllArgsConstructor(staticName = "of")
public class InstantInterval implements TemporalInterval<Instant, InstantInterval> {

    Instant start;
    Instant end;

    @Override
    public @NonNull InstantInterval withStart(Instant start) {
        return new InstantInterval(start, end);
    }

    @Override
    public @NonNull InstantInterval withEnd(Instant end) {
        return new InstantInterval(start, end);
    }

}
