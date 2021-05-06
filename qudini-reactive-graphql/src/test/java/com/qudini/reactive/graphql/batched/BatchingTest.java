package com.qudini.reactive.graphql.batched;

import lombok.Value;
import org.assertj.core.internal.Failures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.function.Function.identity;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Batching")
class BatchingTest {

    @Test
    @DisplayName("should resolve nullable to-one relationships")
    void toOne() {
        @Value
        class Output {
            String id;
        }
        @Value
        class Input {
            String id;
            Output output;
        }
        var o1 = new Output("o1");
        var o2 = new Output("o2");
        var i1 = new Input("i1", o1);
        var i2 = new Input("i2", o2);
        var i3 = new Input("i3", o2);
        var i4 = new Input("i4", null);
        var results = Batching
                .toOne(
                        Set.of(i1, i2, i3, i4),
                        Input::getOutput,
                        Flux::fromIterable,
                        identity()
                )
                .block();
        assertThat(results).hasSize(3);
        assertThat(results.get(i1)).isEqualTo(o1);
        assertThat(results.get(i2)).isEqualTo(o2);
        assertThat(results.get(i3)).isEqualTo(o2);
    }

    @Test
    @DisplayName("should resolve optional to-one relationships")
    void toOptionalOne() {
        @Value
        class Output {
            String id;
        }
        @Value
        class Input {
            String id;
            Optional<Output> output;
        }
        var o1 = new Output("o1");
        var o2 = new Output("o2");
        var i1 = new Input("i1", Optional.of(o1));
        var i2 = new Input("i2", Optional.of(o2));
        var i3 = new Input("i3", Optional.of(o2));
        var i4 = new Input("i4", Optional.empty());
        var results = Batching
                .toOptionalOne(
                        Set.of(i1, i2, i3, i4),
                        Input::getOutput,
                        Flux::fromIterable,
                        identity()
                )
                .block();
        assertThat(results).hasSize(3);
        assertThat(results.get(i1)).isEqualTo(o1);
        assertThat(results.get(i2)).isEqualTo(o2);
        assertThat(results.get(i3)).isEqualTo(o2);
    }

    @Test
    @DisplayName("should resolve to-one without outputs")
    void toOneWithoutOutputs() {
        @Value
        class Output {
            String id;
        }
        @Value
        class Input {
            String id;
            Output output;
        }
        var o1 = new Output("o1");
        var o2 = new Output("o2");
        var i1 = new Input("i1", o1);
        var i2 = new Input("i2", o2);
        var results = Batching
                .toOne(
                        Set.of(i1, i2),
                        Input::getOutput,
                        x -> Flux.just(o2),
                        identity()
                )
                .block();
        assertThat(results).hasSize(1);
        assertThat(results.get(i2)).isEqualTo(o2);
    }

    @Test
    @DisplayName("should resolve to-many relationships")
    void toMany() {
        @Value
        class Input {
            String id;
        }
        @Value
        class Output {
            String id;
            Input input;
        }
        var i1 = new Input("i1");
        var i2 = new Input("i2");
        var i3 = new Input("i3");
        var o1 = new Output("o1", i1);
        var o2 = new Output("o2", i2);
        var o3 = new Output("o3", i2);
        var results = Batching
                .toMany(
                        Set.of(i1, i2, i3),
                        identity(),
                        inputs -> Flux.just(o3, o1, o2),
                        Output::getInput
                )
                .block();
        assertThat(results).hasSize(3);
        assertThat(results.get(i1)).containsExactlyElementsOf(List.of(o1));
        assertThat(results.get(i2)).containsExactlyElementsOf(List.of(o3, o2));
        assertThat(results.get(i3)).isEmpty();
    }

    @Test
    @DisplayName("should not fetch outputs if there are no ids when resolving a to-one relationships")
    void toOneWithoutOutputIds() {
        @Value
        class Output {
            String id;
        }
        @Value
        class Input {
            String id;
            Output output;
        }
        var i = new Input("i", null);
        var results = Batching
                .toOne(
                        Set.of(i),
                        Input::getOutput,
                        x -> {
                            throw Failures.instance().failure("should not have been called");
                        },
                        identity()
                )
                .block();
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("should not fetch outputs if there are no ids when resolving a to-many relationships")
    void toManyWithoutOutputIds() {
        @Value
        class Input {
            String id;
        }
        @Value
        class Output {
            String id;
            Input input;
        }
        var results = Batching
                .toMany(
                        Set.of(),
                        identity(),
                        x -> {
                            throw Failures.instance().failure("should not have been called");
                        },
                        Output::getInput
                )
                .block();
        assertThat(results).isEmpty();
    }

}
