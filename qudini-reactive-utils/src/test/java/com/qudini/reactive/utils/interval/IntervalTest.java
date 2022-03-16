package com.qudini.reactive.utils.interval;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qudini.reactive.utils.intervals.number.IntInterval;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class IntervalTest {

    @Test
    void isPositive() {
        assertThat(IntInterval.of(1, 3).isPositive()).isTrue();
        assertThat(IntInterval.of(3, 1).isPositive()).isFalse();
        assertThat(IntInterval.of(2, 2).isPositive()).isFalse();
    }

    @Test
    void isNegative() {
        assertThat(IntInterval.of(1, 3).isNegative()).isFalse();
        assertThat(IntInterval.of(3, 1).isNegative()).isTrue();
        assertThat(IntInterval.of(2, 2).isNegative()).isFalse();
    }

    @Test
    void isEmpty() {
        assertThat(IntInterval.of(1, 3).isEmpty()).isFalse();
        assertThat(IntInterval.of(3, 1).isEmpty()).isFalse();
        assertThat(IntInterval.of(2, 2).isEmpty()).isTrue();
    }

    @Test
    void overlaps() {
        assertThat(IntInterval.of(1, 3).overlaps(IntInterval.of(2, 4))).isTrue();
        assertThat(IntInterval.of(1, 4).overlaps(IntInterval.of(2, 3))).isTrue();
        assertThat(IntInterval.of(1, 2).overlaps(IntInterval.of(2, 3))).isFalse();
    }

    @Test
    void isContiguousWith() {
        assertThat(IntInterval.of(1, 2).isContiguousWith(IntInterval.of(2, 3))).isTrue();
        assertThat(IntInterval.of(1, 3).isContiguousWith(IntInterval.of(2, 4))).isFalse();
        assertThat(IntInterval.of(1, 2).isContiguousWith(IntInterval.of(3, 4))).isFalse();
    }

    @Test
    void contains() {
        assertThat(IntInterval.of(1, 3).contains(IntInterval.of(1, 2))).isTrue();
        assertThat(IntInterval.of(1, 3).contains(IntInterval.of(1, 3))).isTrue();
        assertThat(IntInterval.of(1, 3).contains(IntInterval.of(1, 4))).isFalse();
    }

    @Test
    void serialisable() throws JsonProcessingException {
        var interval = IntInterval.of(1, 2);
        var json = new ObjectMapper().writeValueAsString(interval);
        var parsed = new ObjectMapper().readValue(json, Map.class);
        assertThat(parsed).containsExactlyInAnyOrderEntriesOf(Map.of(
                "start", 1,
                "end", 2
        ));
    }

    @Test
    void parsable() throws JsonProcessingException {
        var json = "{\"start\": 1, \"end\": 2}";
        var parsed = new ObjectMapper().readValue(json, IntInterval.class);
        assertThat(parsed).isEqualTo(IntInterval.of(1, 2));
    }

}
