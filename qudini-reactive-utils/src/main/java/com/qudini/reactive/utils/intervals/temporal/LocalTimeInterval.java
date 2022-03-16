package com.qudini.reactive.utils.intervals.temporal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalTime;
import java.time.temporal.TemporalUnit;

@Value
@Builder
@Jacksonized
@AllArgsConstructor(staticName = "of")
public class LocalTimeInterval implements TemporalInterval<LocalTime, LocalTimeInterval> {

    LocalTime start;
    LocalTime end;

    @Override
    public @NonNull LocalTimeInterval withStart(LocalTime start) {
        return new LocalTimeInterval(start, end);
    }

    @Override
    public @NonNull LocalTimeInterval withEnd(LocalTime end) {
        return new LocalTimeInterval(start, end);
    }

}
