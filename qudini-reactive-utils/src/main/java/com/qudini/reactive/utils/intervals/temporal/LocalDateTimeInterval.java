package com.qudini.reactive.utils.intervals.temporal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;

@Value
@Builder
@Jacksonized
@AllArgsConstructor(staticName = "of")
public class LocalDateTimeInterval implements TemporalInterval<LocalDateTime, LocalDateTimeInterval> {

    LocalDateTime start;
    LocalDateTime end;

    @Override
    public @NonNull LocalDateTimeInterval withStart(LocalDateTime start) {
        return new LocalDateTimeInterval(start, end);
    }

    @Override
    public @NonNull LocalDateTimeInterval withEnd(LocalDateTime end) {
        return new LocalDateTimeInterval(start, end);
    }

}
