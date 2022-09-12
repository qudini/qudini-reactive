package com.qudini.reactive.utils.intervals;

public interface UpdatableInterval<E extends Comparable<? super E>, T extends UpdatableInterval<E, T>> extends Interval<E, T> {

    T withStart(E start);

    T withEnd(E end);

}
