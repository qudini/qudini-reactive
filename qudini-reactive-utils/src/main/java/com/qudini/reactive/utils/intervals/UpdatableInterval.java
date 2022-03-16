package com.qudini.reactive.utils.intervals;

import javax.annotation.Nonnull;

public interface UpdatableInterval<E extends Comparable<? super E>, T extends UpdatableInterval<E, T>> extends Interval<E, T> {

    @Nonnull
    T withStart(E start);

    @Nonnull
    T withEnd(E end);

}
