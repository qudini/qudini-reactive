package com.qudini.reactive.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.AbstractMap;
import java.util.concurrent.atomic.AtomicReference;

import static com.qudini.reactive.utils.MoreTuples.both;
import static com.qudini.reactive.utils.MoreTuples.each;
import static com.qudini.reactive.utils.MoreTuples.ifBoth;
import static com.qudini.reactive.utils.MoreTuples.ifEach;
import static com.qudini.reactive.utils.MoreTuples.ifEither;
import static com.qudini.reactive.utils.MoreTuples.ifLeft;
import static com.qudini.reactive.utils.MoreTuples.ifRight;
import static com.qudini.reactive.utils.MoreTuples.left;
import static com.qudini.reactive.utils.MoreTuples.right;
import static com.qudini.reactive.utils.MoreTuples.takeBoth;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MoreTuples")
class MoreTuplesTest {

    @Test
    @DisplayName("should allow building a Tuple2 from a Map.Entry")
    void fromEntry() {
        var entry = new AbstractMap.SimpleEntry<>("foo", "bar");
        var tuple = MoreTuples.fromEntry(entry);
        assertThat(tuple).isEqualTo(Tuples.of("foo", "bar"));
    }

    @Test
    @DisplayName("should allow building a Tuple2 from a two-element array")
    void fromArray() {
        var array = new String[]{"foo", "bar"};
        var tuple = MoreTuples.fromArray(array);
        assertThat(tuple).isEqualTo(Tuples.of("foo", "bar"));
    }

    @Test
    @DisplayName("should allow reducing to the left value of a Tuple2")
    void leftReducer() {
        var output = createFooBar()
                .map(left())
                .block();
        assertThat(output).isEqualTo("foo");
    }

    @Test
    @DisplayName("should allow reducing to the right value of a Tuple2")
    void rightReducer() {
        var output = createFooBar()
                .map(right())
                .block();
        assertThat(output).isEqualTo("bar");
    }

    @Test
    @DisplayName("should allow mapping on each value of a Tuple2")
    void eachMapper() {
        var output = createFooBar()
                .map(each(x -> x + "bar"))
                .block();
        assertThat(output).isEqualTo(Tuples.of("foobar", "barbar"));
    }

    @Test
    @DisplayName("should allow mapping on each value of a Tuple2 with a predicate")
    void eachPredicate() {
        var output = createFooBar()
                .filter(ifEach(x -> 3 == x.length()))
                .block();
        assertThat(output).isEqualTo(Tuples.of("foo", "bar"));
    }

    @Test
    @DisplayName("should allow mapping on either value of a Tuple2 with a predicate")
    void eitherPredicate() {
        var output = createFooBar()
                .filter(ifEither("bar"::equals))
                .block();
        assertThat(output).isEqualTo(Tuples.of("foo", "bar"));
    }

    @Test
    @DisplayName("should allow mapping a Tuple2 with a bifunction")
    void bothMapper() {
        var output = createFooBar()
                .map(both((foo, bar) -> foo + bar))
                .block();
        assertThat(output).isEqualTo("foobar");
    }

    @Test
    @DisplayName("should allow mapping a Tuple2 with a biconsumer")
    void takeBothMapper() {
        var tuple = new AtomicReference<Tuple2<String, String>>();
        createFooBar()
                .doOnNext(takeBoth((foo, bar) -> tuple.set(Tuples.of(foo, bar))))
                .block();
        assertThat(tuple.get()).isEqualTo(Tuples.of("foo", "bar"));
    }

    @Test
    @DisplayName("should allow mapping a Tuple2 with a bipredicate")
    void ifBothPredicate() {
        var output = createFooBar()
                .filter(ifBoth((foo, bar) -> true))
                .block();
        assertThat(output).isEqualTo(Tuples.of("foo", "bar"));
    }

    @Test
    @DisplayName("should allow mapping on the left value of a Tuple2 with a function")
    void leftMapper() {
        var output = createFooBar()
                .map(left(foo -> foo + "bar"))
                .block();
        assertThat(output).isEqualTo(Tuples.of("foobar", "bar"));
    }

    @Test
    @DisplayName("should allow mapping on the left value of a Tuple2 with a predicate")
    void ifLeftPredicate() {
        var output = createFooBar()
                .filter(ifLeft("foo"::equals))
                .block();
        assertThat(output).isEqualTo(Tuples.of("foo", "bar"));
    }

    @Test
    @DisplayName("should allow mapping on the right value of a Tuple2 with a function")
    void rightMapper() {
        var output = createFooBar()
                .map(right(bar -> bar + "bar"))
                .block();
        assertThat(output).isEqualTo(Tuples.of("foo", "barbar"));
    }

    @Test
    @DisplayName("should allow mapping on the right value of a Tuple2 with a predicate")
    void ifRightPredicate() {
        var output = createFooBar()
                .filter(ifRight("bar"::equals))
                .block();
        assertThat(output).isEqualTo(Tuples.of("foo", "bar"));
    }

    private Mono<Tuple2<String, String>> createFooBar() {
        return Mono.zip(
                Mono.just("foo"),
                Mono.just("bar")
        );
    }

}
