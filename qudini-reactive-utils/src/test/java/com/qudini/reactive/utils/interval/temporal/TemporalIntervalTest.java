package com.qudini.reactive.utils.interval.temporal;

import com.qudini.reactive.utils.intervals.temporal.InstantInterval;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TemporalIntervalTest {

    static final Instant FROM = Instant.parse("2007-12-03T10:15:30Z");
    static final Instant TO = FROM.plus(1, MINUTES);
    static final InstantInterval ONE_MIN_INTERVAL = InstantInterval.of(FROM, TO);

    @Test
    void isLessThan() {
        assertThat(ONE_MIN_INTERVAL.isLessThan(Duration.ofSeconds(61))).isTrue();
        assertThat(ONE_MIN_INTERVAL.isLessThan(Duration.ofSeconds(60))).isFalse();
        assertThat(ONE_MIN_INTERVAL.isLessThan(Duration.ofSeconds(59))).isFalse();
    }

    @Test
    void isLessThanOrEqualTo() {
        assertThat(ONE_MIN_INTERVAL.isLessThanOrEqualTo(Duration.ofSeconds(61))).isTrue();
        assertThat(ONE_MIN_INTERVAL.isLessThanOrEqualTo(Duration.ofSeconds(60))).isTrue();
        assertThat(ONE_MIN_INTERVAL.isLessThanOrEqualTo(Duration.ofSeconds(59))).isFalse();
    }

    @Test
    void isGreaterThan() {
        assertThat(ONE_MIN_INTERVAL.isGreaterThan(Duration.ofSeconds(61))).isFalse();
        assertThat(ONE_MIN_INTERVAL.isGreaterThan(Duration.ofSeconds(60))).isFalse();
        assertThat(ONE_MIN_INTERVAL.isGreaterThan(Duration.ofSeconds(59))).isTrue();
    }

    @Test
    void isGreaterThanOrEqualTo() {
        assertThat(ONE_MIN_INTERVAL.isGreaterThanOrEqualTo(Duration.ofSeconds(61))).isFalse();
        assertThat(ONE_MIN_INTERVAL.isGreaterThanOrEqualTo(Duration.ofSeconds(60))).isTrue();
        assertThat(ONE_MIN_INTERVAL.isGreaterThanOrEqualTo(Duration.ofSeconds(59))).isTrue();
    }

    @Test
    void isEqualTo() {
        assertThat(ONE_MIN_INTERVAL.isEqualTo(Duration.ofSeconds(61))).isFalse();
        assertThat(ONE_MIN_INTERVAL.isEqualTo(Duration.ofSeconds(60))).isTrue();
        assertThat(ONE_MIN_INTERVAL.isEqualTo(Duration.ofSeconds(59))).isFalse();
    }

    @Test
    void shiftIfStartsBefore() {
        assertThat(ONE_MIN_INTERVAL.shiftIfStartsBefore(FROM.minus(5, MINUTES))).isEqualTo(ONE_MIN_INTERVAL);
        assertThat(ONE_MIN_INTERVAL.shiftIfStartsBefore(FROM.plus(5, MINUTES))).isEqualTo(InstantInterval.of(
                FROM.plus(5, MINUTES),
                FROM.plus(6, MINUTES)
        ));
    }

    @Test
    void shiftIfEndsAfter() {
        assertThat(ONE_MIN_INTERVAL.shiftIfEndsAfter(TO.plus(5, MINUTES))).isEqualTo(ONE_MIN_INTERVAL);
        assertThat(ONE_MIN_INTERVAL.shiftIfEndsAfter(TO.minus(5, MINUTES))).isEqualTo(InstantInterval.of(
                TO.minus(6, MINUTES),
                TO.minus(5, MINUTES)
        ));
    }

}
