package com.qudini.reactive.utils.intervals.temporal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.OffsetDateTime;

@Value
@Builder
@Jacksonized
@AllArgsConstructor(staticName = "of")
public class OffsetDateTimeInterval implements TemporalInterval<OffsetDateTime, OffsetDateTimeInterval> {

    OffsetDateTime start;
    OffsetDateTime end;

    @Override
    public @NonNull OffsetDateTimeInterval withStart(OffsetDateTime start) {
        return new OffsetDateTimeInterval(start, end);
    }

    @Override
    public @NonNull OffsetDateTimeInterval withEnd(OffsetDateTime end) {
        return new OffsetDateTimeInterval(start, end);
    }

}
