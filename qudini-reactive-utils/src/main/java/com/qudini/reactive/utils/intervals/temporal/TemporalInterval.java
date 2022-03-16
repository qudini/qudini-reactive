package com.qudini.reactive.utils.intervals.temporal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qudini.reactive.utils.MoreComparables;
import com.qudini.reactive.utils.intervals.UpdatableInterval;

import java.time.Duration;
import java.time.temporal.Temporal;

public interface TemporalInterval<E extends Temporal & Comparable<? super E>, T extends TemporalInterval<E, T>> extends UpdatableInterval<E, T> {

    /**
     * <p>Returns the duration of this interval.</p>
     */
    @JsonIgnore
    default Duration getDuration() {
        return Duration.between(getStart(), getEnd());
    }

    /**
     * <p>Whether the duration between this interval's start and end is strictly less that the given duration.</p>
     */
    default boolean isLessThan(Duration duration) {
        return MoreComparables.isLessThan(getDuration(), duration);
    }

    /**
     * <p>Whether the duration between this interval's start and end is less that or equal to the given duration.</p>
     */
    default boolean isLessThanOrEqualTo(Duration duration) {
        return MoreComparables.isLessThanOrEqualTo(getDuration(), duration);
    }

    /**
     * <p>Whether the duration between this interval's start and end is strictly greater that the given duration.</p>
     */
    default boolean isGreaterThan(Duration duration) {
        return MoreComparables.isGreaterThan(getDuration(), duration);
    }

    /**
     * <p>Whether the duration between this interval's start and end is greater than or equal to that the given duration.</p>
     */
    default boolean isGreaterThanOrEqualTo(Duration duration) {
        return MoreComparables.isGreaterThanOrEqualTo(getDuration(), duration);
    }

    /**
     * <p>Whether the duration between this interval's start and end is equal to the given duration.</p>
     */
    default boolean isEqualTo(Duration duration) {
        return getDuration().equals(duration);
    }

    /**
     * <p>If this interval starts before the given minStart,
     * postpones it so that the returned interval starts at minStart and ends at minStart+duration.</p>
     */
    default T shiftIfStartsBefore(E minStart) {
        return MoreComparables.isLessThan(getStart(), minStart)
                ? withStart(minStart).withEnd((E) minStart.plus(getDuration()))
                : (T) this;
    }

    /**
     * <p>If this interval ends after the given maxEnd,
     * prepones it so that the returned interval starts at maxEnd-duration and ends at maxEnd.</p>
     */
    default T shiftIfEndsAfter(E maxEnd) {
        return MoreComparables.isGreaterThan(getEnd(), maxEnd)
                ? withStart((E) maxEnd.minus(getDuration())).withEnd(maxEnd)
                : (T) this;
    }

}
