package com.qudini.reactive.utils;

import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

/**
 * <p>Utilities around monos.</p>
 */
@NoArgsConstructor(access = PRIVATE)
public final class MoreMonos {

    /**
     * <p>Transforms an empty mono into a valued mono of an empty optional,
     * and a valued mono into a valued mono of a valued optional.</p>
     * <p>Example:
     * <pre>{@literal
     * Mono<Optional<T>> example(Mono<T> mono) {
     *     return mono.transform(MoreMonos::toOptional);
     * }
     * }</pre>
     * </p>
     */
    public static <T> Mono<Optional<T>> toOptional(Mono<T> mono) {
        return mono
                .map(Optional::of)
                .switchIfEmpty(Mono.fromSupplier(Optional::empty));
    }

}
