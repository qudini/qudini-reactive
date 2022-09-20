package com.qudini.reactive.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qudini.utils.MoreJackson;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class MoreWebClients {

    /**
     * <p>Creates a new {@link WebClient} using the {@link ObjectMapper} returned by {@link MoreJackson#newObjectMapper()}
     * for both the encoder ({@link Jackson2JsonEncoder}) and the decoder ({@link Jackson2JsonDecoder}).</p>
     */
    public static WebClient newWebClient() {
        return newWebClient(MoreJackson.newObjectMapper());
    }

    /**
     * <p>Creates a new {@link WebClient} using the given {@link ObjectMapper}
     * for both the encoder ({@link Jackson2JsonEncoder}) and the decoder ({@link Jackson2JsonDecoder}).</p>
     */
    public static WebClient newWebClient(ObjectMapper mapper) {
        var exchangeStrategies = ExchangeStrategies
                .builder()
                .codecs(configurer -> configureWebClient(configurer, mapper))
                .build();
        return WebClient
                .builder()
                .exchangeStrategies(exchangeStrategies)
                .build();
    }

    private static void configureWebClient(ClientCodecConfigurer configurer, ObjectMapper mapper) {
        var codecs = configurer.defaultCodecs();
        codecs.jackson2JsonEncoder(new Jackson2JsonEncoder(mapper));
        codecs.jackson2JsonDecoder(new Jackson2JsonDecoder(mapper));
    }

}
