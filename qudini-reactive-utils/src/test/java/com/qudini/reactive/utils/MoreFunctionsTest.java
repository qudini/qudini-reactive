package com.qudini.reactive.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.Serial;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.qudini.reactive.utils.MoreFunctions.throwableBiConsumer;
import static com.qudini.reactive.utils.MoreFunctions.throwableBiFunction;
import static com.qudini.reactive.utils.MoreFunctions.throwableBiPredicate;
import static com.qudini.reactive.utils.MoreFunctions.throwableConsumer;
import static com.qudini.reactive.utils.MoreFunctions.throwableFunction;
import static com.qudini.reactive.utils.MoreFunctions.throwablePredicate;
import static com.qudini.reactive.utils.MoreFunctions.throwableSupplier;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("MoreFunctions")
class MoreFunctionsTest {

    @Test
    @DisplayName("should handle successful throwable consumer")
    void consumerSuccess() {
        takeConsumer(throwableConsumer(i -> {
        }));
    }

    @Test
    @DisplayName("should handle erroneous throwable consumer")
    void consumerError() {
        assertThrows(
                TestException.class,
                () -> takeConsumer(throwableConsumer(i -> {
                    throw new TestException();
                }))
        );
    }

    @Test
    @DisplayName("should handle successful throwable function")
    void functionSuccess() {
        var output = takeFunction(throwableFunction(i -> 42));
        assertThat(output).isEqualTo(42);
    }

    @Test
    @DisplayName("should handle erroneous throwable function")
    void functionError() {
        assertThrows(
                TestException.class,
                () -> takeFunction(throwableFunction(i -> {
                    throw new TestException();
                }))
        );
    }

    @Test
    @DisplayName("should handle successful throwable supplier")
    void supplierSuccess() {
        var output = takeSupplier(throwableSupplier(() -> 42));
        assertThat(output).isEqualTo(42);
    }

    @Test
    @DisplayName("should handle erroneous throwable supplier")
    void supplierError() {
        assertThrows(
                TestException.class,
                () -> takeSupplier(throwableSupplier(() -> {
                    throw new TestException();
                }))
        );
    }

    @Test
    @DisplayName("should handle successful throwable predicate")
    void predicateSuccess() {
        var output = takePredicate(throwablePredicate(i -> true));
        assertThat(output).isTrue();
    }

    @Test
    @DisplayName("should handle erroneous throwable predicate")
    void predicateError() {
        assertThrows(
                TestException.class,
                () -> takePredicate(throwablePredicate(i -> {
                    throw new TestException();
                }))
        );
    }

    @Test
    @DisplayName("should handle successful throwable biconsumer")
    void biConsumerSuccess() {
        takeBiConsumer(throwableBiConsumer((i, j) -> {
        }));
    }

    @Test
    @DisplayName("should handle erroneous throwable biconsumer")
    void biConsumerError() {
        assertThrows(
                TestException.class,
                () -> takeBiConsumer(throwableBiConsumer((i, j) -> {
                    throw new TestException();
                }))
        );
    }

    @Test
    @DisplayName("should handle successful throwable bifunction")
    void biFunctionSuccess() {
        var output = takeBiFunction(throwableBiFunction((i, j) -> 42));
        assertThat(output).isEqualTo(42);
    }

    @Test
    @DisplayName("should handle erroneous throwable bifunction")
    void biFunctionError() {
        assertThrows(
                TestException.class,
                () -> takeBiFunction(throwableBiFunction((i, j) -> {
                    throw new TestException();
                }))
        );
    }

    @Test
    @DisplayName("should handle successful throwable bipredicate")
    void biPredicateSuccess() {
        var output = takeBiPredicate(throwableBiPredicate((i, j) -> true));
        assertThat(output).isTrue();
    }

    @Test
    @DisplayName("should handle erroneous throwable bipredicate")
    void biPredicateError() {
        assertThrows(
                TestException.class,
                () -> takeBiPredicate(throwableBiPredicate((i, j) -> {
                    throw new TestException();
                }))
        );
    }

    private void takeConsumer(Consumer<Integer> consumer) {
        consumer.accept(42);
    }

    private int takeFunction(Function<Integer, Integer> function) {
        return function.apply(42);
    }

    private int takeSupplier(Supplier<Integer> supplier) {
        return supplier.get();
    }

    private boolean takePredicate(Predicate<Integer> predicate) {
        return predicate.test(42);
    }

    private void takeBiConsumer(BiConsumer<Integer, Integer> consumer) {
        consumer.accept(42, 42);
    }

    private int takeBiFunction(BiFunction<Integer, Integer, Integer> function) {
        return function.apply(42, 42);
    }

    private boolean takeBiPredicate(BiPredicate<Integer, Integer> predicate) {
        return predicate.test(42, 42);
    }

    private static final class TestException extends Exception {

        @Serial
        private static final long serialVersionUID = 1L;

    }

}
