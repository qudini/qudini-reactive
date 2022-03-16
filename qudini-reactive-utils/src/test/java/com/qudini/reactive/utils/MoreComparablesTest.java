package com.qudini.reactive.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class MoreComparablesTest {

    @Test
    void leastBetween() {
        assertThat(MoreComparables.leastBetween(1, 2)).isEqualTo(1);
        assertThat(MoreComparables.leastBetween(2, 1)).isEqualTo(1);
    }

    @Test
    void greatestBetween() {
        assertThat(MoreComparables.greatestBetween(1, 2)).isEqualTo(2);
        assertThat(MoreComparables.greatestBetween(2, 1)).isEqualTo(2);
    }

    @Test
    void isLessThan() {
        assertThat(MoreComparables.isLessThan(1, 2)).isTrue();
        assertThat(MoreComparables.isLessThan(1, 1)).isFalse();
        assertThat(MoreComparables.isLessThan(2, 1)).isFalse();
    }

    @Test
    void isLessThanOrEqualTo() {
        assertThat(MoreComparables.isLessThanOrEqualTo(1, 2)).isTrue();
        assertThat(MoreComparables.isLessThanOrEqualTo(1, 1)).isTrue();
        assertThat(MoreComparables.isLessThanOrEqualTo(2, 1)).isFalse();
    }

    @Test
    void isGreaterThan() {
        assertThat(MoreComparables.isGreaterThan(2, 1)).isTrue();
        assertThat(MoreComparables.isGreaterThan(1, 1)).isFalse();
        assertThat(MoreComparables.isGreaterThan(1, 2)).isFalse();
    }

    @Test
    void isGreaterThanOrEqualTo() {
        assertThat(MoreComparables.isGreaterThanOrEqualTo(2, 1)).isTrue();
        assertThat(MoreComparables.isGreaterThanOrEqualTo(1, 1)).isTrue();
        assertThat(MoreComparables.isGreaterThanOrEqualTo(1, 2)).isFalse();
    }

}
