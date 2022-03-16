package com.qudini.reactive.utils.interval.number;

import com.qudini.reactive.utils.intervals.number.ByteInterval;
import com.qudini.reactive.utils.intervals.number.DoubleInterval;
import com.qudini.reactive.utils.intervals.number.FloatInterval;
import com.qudini.reactive.utils.intervals.number.IntInterval;
import com.qudini.reactive.utils.intervals.number.LongInterval;
import com.qudini.reactive.utils.intervals.number.ShortInterval;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NumberIntervalTest {

    @Test
    void toByteInterval() {
        assertThat(IntInterval.of(1, 2).toByteInterval()).isEqualTo(ByteInterval.of((byte) 1, (byte) 2));
    }

    @Test
    void toDoubleInterval() {
        assertThat(IntInterval.of(1, 2).toDoubleInterval()).isEqualTo(DoubleInterval.of(1d, 2d));
    }

    @Test
    void toFloatInterval() {
        assertThat(IntInterval.of(1, 2).toFloatInterval()).isEqualTo(FloatInterval.of(1f, 2f));
    }

    @Test
    void toIntInterval() {
        assertThat(IntInterval.of(1, 2).toIntInterval()).isEqualTo(IntInterval.of(1, 2));
    }

    @Test
    void toLongInterval() {
        assertThat(IntInterval.of(1, 2).toLongInterval()).isEqualTo(LongInterval.of(1L, 2L));
    }

    @Test
    void toShortInterval() {
        assertThat(IntInterval.of(1, 2).toShortInterval()).isEqualTo(ShortInterval.of((short) 1, (short) 2));
    }

}
