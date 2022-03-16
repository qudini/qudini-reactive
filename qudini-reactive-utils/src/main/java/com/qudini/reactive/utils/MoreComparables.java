package com.qudini.reactive.utils;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class MoreComparables {

    public static <T extends Comparable<? super T>> T leastBetween(T a, T b) {
        return isLessThan(a, b) ? a : b;
    }

    public static <T extends Comparable<? super T>> T greatestBetween(T a, T b) {
        return isGreaterThan(a, b) ? a : b;
    }

    public static <T extends Comparable<? super T>> boolean isLessThan(T a, T b) {
        return a.compareTo(b) < 0;
    }

    public static <T extends Comparable<? super T>> boolean isLessThanOrEqualTo(T a, T b) {
        return a.compareTo(b) <= 0;
    }

    public static <T extends Comparable<? super T>> boolean isGreaterThan(T a, T b) {
        return a.compareTo(b) > 0;
    }

    public static <T extends Comparable<? super T>> boolean isGreaterThanOrEqualTo(T a, T b) {
        return a.compareTo(b) >= 0;
    }

}
