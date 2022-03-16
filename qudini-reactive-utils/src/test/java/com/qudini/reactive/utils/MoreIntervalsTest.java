package com.qudini.reactive.utils;

import com.qudini.reactive.utils.intervals.number.IntInterval;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MoreIntervalsTest {

    @Test
    void enclose() {
        var intervals = List.of(
                IntInterval.of(0, 1), // removed
                IntInterval.of(0, 2), // removed
                IntInterval.of(1, 3), // shortened
                IntInterval.of(2, 4), // kept
                IntInterval.of(3, 4), // kept
                IntInterval.of(3, 5), // kept
                IntInterval.of(4, 6), // shortened
                IntInterval.of(5, 7), // removed
                IntInterval.of(6, 7), // removed
                IntInterval.of(1, 6) // shortened
        );
        var enclosed = MoreIntervals.enclose(intervals, IntInterval.of(2, 5));
        assertThat(enclosed).containsExactlyInAnyOrder(
                IntInterval.of(2, 3),
                IntInterval.of(2, 4),
                IntInterval.of(3, 4),
                IntInterval.of(3, 5),
                IntInterval.of(4, 5),
                IntInterval.of(2, 5)
        );
    }

    @Test
    void mergeTwoOverlapping() {
        var intervals = List.of(
                IntInterval.of(1, 3),
                IntInterval.of(2, 4)
        );
        var merged = MoreIntervals.merge(intervals);
        assertThat(merged).containsExactlyInAnyOrder(
                IntInterval.of(1, 4)
        );
    }

    @Test
    void mergeTwoContiguous() {
        var intervals = List.of(
                IntInterval.of(1, 2),
                IntInterval.of(2, 3)
        );
        var merged = MoreIntervals.merge(intervals);
        assertThat(merged).containsExactlyInAnyOrder(
                IntInterval.of(1, 3)
        );
    }

    @Test
    void mergeTwoContained() {
        var intervals = List.of(
                IntInterval.of(1, 4),
                IntInterval.of(2, 3)
        );
        var merged = MoreIntervals.merge(intervals);
        assertThat(merged).containsExactlyInAnyOrder(
                IntInterval.of(1, 4)
        );
    }

    @Test
    void mergeTwoNotOverlapping() {
        var intervals = List.of(
                IntInterval.of(1, 2),
                IntInterval.of(3, 4)
        );
        var merged = MoreIntervals.merge(intervals);
        assertThat(merged).containsExactlyInAnyOrder(
                IntInterval.of(1, 2),
                IntInterval.of(3, 4)
        );
    }

    @Test
    void mergeThree() {
        var intervals = List.of(
                IntInterval.of(1, 3),
                IntInterval.of(2, 4),
                IntInterval.of(0, 1)
        );
        var merged = MoreIntervals.merge(intervals);
        assertThat(merged).containsExactlyInAnyOrder(
                IntInterval.of(0, 4)
        );
    }

    @Test
    void subtractOneFromMany() {
        var intervals = List.of(
                IntInterval.of(0, 1), // kept
                IntInterval.of(1, 2), // kept
                IntInterval.of(1, 3), // shortened
                IntInterval.of(1, 6), // split
                IntInterval.of(1, 5), // shortened
                IntInterval.of(2, 3), // removed
                IntInterval.of(3, 4), // removed
                IntInterval.of(4, 5), // removed
                IntInterval.of(2, 6), // shortened
                IntInterval.of(4, 6), // shortened
                IntInterval.of(5, 6), // kept
                IntInterval.of(6, 7) // kept
        );
        var subtracted = MoreIntervals.subtract(intervals, IntInterval.of(2, 5));
        assertThat(subtracted).containsExactlyInAnyOrder(
                IntInterval.of(0, 1),
                IntInterval.of(1, 2),
                IntInterval.of(1, 2),
                IntInterval.of(1, 2),
                IntInterval.of(1, 2),
                IntInterval.of(5, 6),
                IntInterval.of(5, 6),
                IntInterval.of(5, 6),
                IntInterval.of(5, 6),
                IntInterval.of(6, 7)
        );
    }

    @Test
    void subtractManyFromMany() {
        var intervals = List.of(
                IntInterval.of(1, 4), // removed
                IntInterval.of(2, 3), // removed
                IntInterval.of(0, 7) // split
        );
        var subtracted = MoreIntervals.subtract(intervals, List.of(
                IntInterval.of(1, 2),
                IntInterval.of(2, 4),
                IntInterval.of(5, 6)
        ));
        assertThat(subtracted).containsExactlyInAnyOrder(
                IntInterval.of(0, 1),
                IntInterval.of(4, 5),
                IntInterval.of(6, 7)
        );
    }

}
