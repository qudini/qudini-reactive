package com.qudini.reactive.utils.intervals.number;

import com.qudini.reactive.utils.intervals.UpdatableInterval;

public interface NumberInterval<E extends Number & Comparable<? super E>, T extends NumberInterval<E, T>> extends UpdatableInterval<E, T> {

    default ByteInterval toByteInterval() {
        return ByteInterval.of(getStart().byteValue(), getEnd().byteValue());
    }

    default DoubleInterval toDoubleInterval() {
        return DoubleInterval.of(getStart().doubleValue(), getEnd().doubleValue());
    }

    default FloatInterval toFloatInterval() {
        return FloatInterval.of(getStart().floatValue(), getEnd().floatValue());
    }

    default IntInterval toIntInterval() {
        return IntInterval.of(getStart().intValue(), getEnd().intValue());
    }

    default LongInterval toLongInterval() {
        return LongInterval.of(getStart().longValue(), getEnd().longValue());
    }

    default ShortInterval toShortInterval() {
        return ShortInterval.of(getStart().shortValue(), getEnd().shortValue());
    }

}
