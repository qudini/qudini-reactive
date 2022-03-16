package com.qudini.reactive.utils;

import com.qudini.reactive.utils.intervals.Interval;
import com.qudini.reactive.utils.intervals.UpdatableInterval;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.qudini.reactive.utils.MoreComparables.greatestBetween;
import static com.qudini.reactive.utils.MoreComparables.isGreaterThan;
import static com.qudini.reactive.utils.MoreComparables.isLessThan;
import static com.qudini.reactive.utils.MoreComparables.isLessThanOrEqualTo;
import static com.qudini.reactive.utils.MoreComparables.leastBetween;
import static java.util.stream.Collectors.toUnmodifiableList;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class MoreIntervals {

    /**
     * <p>Ensures all intervals are within the given bounds, removing/reshaping them if needed.
     * Will not remove resulting duplicates if any, use {@link #merge(List)} if needed.</p>
     */
    public static <E extends Comparable<E>, T extends UpdatableInterval<E, T>> List<T> enclose(List<T> intervals, Interval<E, ?> bounds) {
        return intervals
                .stream()
                .filter(interval -> isGreaterThan(interval.getEnd(), bounds.getStart()))
                .filter(interval -> isLessThan(interval.getStart(), bounds.getEnd()))
                .map(interval -> interval.withStart(greatestBetween(interval.getStart(), bounds.getStart())))
                .map(interval -> interval.withEnd(leastBetween(interval.getEnd(), bounds.getEnd())))
                .collect(toUnmodifiableList());
    }

    /**
     * <p>Merges overlapping/contiguous intervals if any.</p>
     */
    public static <E extends Comparable<E>, T extends UpdatableInterval<E, T>> List<T> merge(List<T> intervals) {
        if (intervals.size() <= 1) {
            return intervals;
        }
        var mergedIntervals = new ArrayList<T>();
        var sortedIntervals = intervals.stream().sorted().collect(toUnmodifiableList());
        var a = sortedIntervals.get(0);
        for (int i = 1, n = intervals.size(); i < n; i++) {
            var b = sortedIntervals.get(i);
            if (a.overlaps(b) || a.isContiguousWith(b)) {
                a = a.withStart(leastBetween(a.getStart(), b.getStart()));
                a = a.withEnd(greatestBetween(a.getEnd(), b.getEnd()));
            } else {
                mergedIntervals.add(a);
                a = b;
            }
        }
        mergedIntervals.add(a);
        return List.copyOf(mergedIntervals);
    }

    /**
     * <p>Removes all subtrahends from the given intervals,
     * so that none of the resulting intervals overlaps with any of the subtrahends.
     * Will not remove resulting duplicates if any, use {@link #merge(List)} if needed.</p>
     */
    public static <E extends Comparable<E>, T extends UpdatableInterval<E, T>> List<T> subtract(List<T> intervals, List<? extends Interval<E, ?>> subtrahends) {
        return subtrahends
                .stream()
                .reduce(
                        intervals,
                        MoreIntervals::subtract,
                        MoreCollectors::throwingMerger
                );
    }

    /**
     * <p>Removes the subtrahend from the given intervals,
     * so that none of the resulting intervals overlaps with the subtrahend.
     * Will not remove resulting duplicates if any, use {@link #merge(List)} if needed.</p>
     */
    public static <E extends Comparable<E>, T extends UpdatableInterval<E, T>> List<T> subtract(List<T> intervals, Interval<E, ?> subtrahend) {
        return intervals
                .stream()
                .flatMap(interval -> subtract(interval, subtrahend))
                .collect(toUnmodifiableList());
    }

    /**
     * <p>Removes the subtrahend from the given interval, resulting in 0, 1 or 2 intervals.</p>
     */
    private static <E extends Comparable<E>, T extends UpdatableInterval<E, T>> Stream<T> subtract(T interval, Interval<E, ?> subtrahend) {
        if (subtrahend.contains(interval)) {
            return Stream.of();
        } else if (interval.contains(subtrahend)) {
            return Stream
                    .of(
                            interval.withEnd(subtrahend.getStart()),
                            interval.withStart(subtrahend.getEnd())
                    )
                    .filter(Interval::isPositive);
        } else if (!interval.overlaps(subtrahend)) {
            return Stream.of(interval);
        } else if (isLessThanOrEqualTo(interval.getStart(), subtrahend.getStart())) {
            return Stream.of(interval.withEnd(subtrahend.getStart()));
        } else {
            return Stream.of(interval.withStart(subtrahend.getEnd()));
        }
    }

}
