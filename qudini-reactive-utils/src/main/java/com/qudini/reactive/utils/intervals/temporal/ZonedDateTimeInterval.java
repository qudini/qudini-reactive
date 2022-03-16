package com.qudini.reactive.utils.intervals.temporal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.ZonedDateTime;

@Value
@Builder
@Jacksonized
@AllArgsConstructor(staticName = "of")
public class ZonedDateTimeInterval implements TemporalInterval<ZonedDateTime, ZonedDateTimeInterval> {

    ZonedDateTime start;
    ZonedDateTime end;

    @Override
    public @NonNull ZonedDateTimeInterval withStart(ZonedDateTime start) {
        return new ZonedDateTimeInterval(start, end);
    }

    @Override
    public @NonNull ZonedDateTimeInterval withEnd(ZonedDateTime end) {
        return new ZonedDateTimeInterval(start, end);
    }

}
