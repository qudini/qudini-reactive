package com.qudini.reactive.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Monos")
class MonosTest {

    @Test
    @DisplayName("should allow transforming a Mono<T> into a Mono<Optional<T>>")
    void toValuedOptional() {
        var result = Mono
                .just(42)
                .transform(Monos::toOptional)
                .block();
        assertThat(result).isEqualTo(Optional.of(42));
    }

    @Test
    @DisplayName("should allow transforming an empty Mono into a valued Mono holding an empty Optional")
    void toEmptyOptional() {
        var result = Mono
                .empty()
                .transform(Monos::toOptional)
                .block();
        assertThat(result).isEqualTo(Optional.empty());
    }

}
