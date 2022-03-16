package com.qudini.reactive.utils.intervals;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ComparisonChain;

import javax.annotation.Nonnull;

import static com.qudini.reactive.utils.MoreComparables.isGreaterThan;
import static com.qudini.reactive.utils.MoreComparables.isGreaterThanOrEqualTo;
import static com.qudini.reactive.utils.MoreComparables.isLessThan;
import static com.qudini.reactive.utils.MoreComparables.isLessThanOrEqualTo;

public interface Interval<E extends Comparable<? super E>, T extends Interval<E, T>> extends Comparable<T> {

    @Nonnull
    E getStart();

    @Nonnull
    E getEnd();

    @JsonIgnore
    default boolean isPositive() {
        return isLessThan(getStart(), getEnd());
    }

    @JsonIgnore
    default boolean isNegative() {
        return isGreaterThan(getStart(), getEnd());
    }

    @JsonIgnore
    default boolean isEmpty() {
        return getStart().equals(getEnd());
    }

    /**
     * Whether this interval overlaps with the given one, for example:
     * <ul>
     * <li>1-3 and 2-4: true</li>
     * <li>1-4 and 2-3: true (see also {@link #contains(Interval)})</li>
     * <li>1-2 and 2-3: false (see also {@link #isContiguousWith(Interval)})</li>
     * </ul>
     */
    default boolean overlaps(Interval<E, ?> o) {
        return isGreaterThan(getStart(), o.getStart()) && isLessThan(getStart(), o.getEnd())
                || isLessThan(getStart(), o.getStart()) && isGreaterThan(getEnd(), o.getStart());
    }

    /**
     * Whether this interval is contiguous with the given one, for example:
     * <ul>
     * <li>1-2 and 2-3: true</li>
     * <li>1-3 and 2-4: false (see also {@link #overlaps(Interval)})</li>
     * <li>1-2 and 3-4: false</li>
     * </ul>
     */
    default boolean isContiguousWith(Interval<E, ?> o) {
        return getStart().equals(o.getEnd()) || getEnd().equals(o.getStart());
    }

    /**
     * Whether this interval entirely contains the given one, for example:
     * <ul>
     * <li>1-3 and 1-2: true</li>
     * <li>1-3 and 1-3: true</li>
     * <li>1-3 and 1-4: false (see also {@link #overlaps(Interval)})</li>
     * </ul>
     */
    default boolean contains(Interval<E, ?> o) {
        return isLessThanOrEqualTo(getStart(), o.getStart()) && isGreaterThanOrEqualTo(getEnd(), o.getEnd());
    }

    @Override
    default int compareTo(T o) {
        return ComparisonChain
                .start()
                .compare(getStart(), o.getStart())
                .compare(getEnd(), o.getEnd())
                .result();
    }

}
