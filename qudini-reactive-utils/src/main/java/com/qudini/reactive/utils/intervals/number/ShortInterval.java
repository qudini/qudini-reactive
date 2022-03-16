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
public class ShortInterval implements NumberInterval<Short, ShortInterval> {

    Short start;
    Short end;

    @Override
    public @NonNull ShortInterval withStart(Short start) {
        return new ShortInterval(start, end);
    }

    @Override
    public @NonNull ShortInterval withEnd(Short end) {
        return new ShortInterval(start, end);
    }

}
