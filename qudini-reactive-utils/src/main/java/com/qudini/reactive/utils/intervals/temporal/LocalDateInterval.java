package com.qudini.reactive.utils.intervals.temporal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

@Value
@Builder
@Jacksonized
@AllArgsConstructor(staticName = "of")
public class LocalDateInterval implements TemporalInterval<LocalDate, LocalDateInterval> {

    LocalDate start;
    LocalDate end;

    @Override
    public @NonNull LocalDateInterval withStart(LocalDate start) {
        return new LocalDateInterval(start, end);
    }

    @Override
    public @NonNull LocalDateInterval withEnd(LocalDate end) {
        return new LocalDateInterval(start, end);
    }

}
