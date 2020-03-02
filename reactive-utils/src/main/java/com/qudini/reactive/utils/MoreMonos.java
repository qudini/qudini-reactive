package com.qudini.reactive.utils;

import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class MoreMonos {

    public static <T> Mono<Optional<T>> toOptional(Mono<T> mono) {
        return mono
                .map(Optional::of)
                .switchIfEmpty(Mono.fromSupplier(Optional::empty));
    }

}
