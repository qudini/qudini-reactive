package com.qudini.reactive.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoreJacksonTest {

    @Test
    void writeIsoDates() throws JsonProcessingException {
        var mapper = MoreJackson.getObjectMapper();
        var value = Instant.parse("2022-03-31T13:47:45.379271Z");
        assertThat(mapper.writeValueAsString(value)).isEqualTo("\"2022-03-31T13:47:45.379271Z\"");
    }

    @Test
    void doNotSerialiseNull() throws JsonProcessingException {
        var mapper = MoreJackson.getObjectMapper();
        Map<String, Object> map = new HashMap<>();
        map.put("foobar", null);
        assertThat(mapper.writeValueAsString(map)).isEqualTo("{}");
    }

    @Test
    void doNotSerialiseEmptyString() throws JsonProcessingException {
        var mapper = MoreJackson.getObjectMapper();
        Map<String, Object> map = new HashMap<>();
        map.put("foobar", "");
        assertThat(mapper.writeValueAsString(map)).isEqualTo("{}");
    }

    @Test
    void doNotSerialiseEmptyCollection() throws JsonProcessingException {
        var mapper = MoreJackson.getObjectMapper();
        Map<String, Object> map = new HashMap<>();
        map.put("foobar", List.of());
        assertThat(mapper.writeValueAsString(map)).isEqualTo("{}");
    }

    @Test
    void doNotSerialiseEmptyMap() throws JsonProcessingException {
        var mapper = MoreJackson.getObjectMapper();
        Map<String, Object> map = new HashMap<>();
        map.put("foobar", Map.of());
        assertThat(mapper.writeValueAsString(map)).isEqualTo("{}");
    }

    @Test
    void parseEmptyStringToNull() throws JsonProcessingException {
        var mapper = MoreJackson.getObjectMapper();
        var parsed = mapper.readValue("{\"foobar\":\"\"}", MoreJackson.toMap());
        assertThat(parsed.containsKey("foobar")).isTrue();
        assertThat(parsed.get("foobar")).isNull();
        assertThrows(UnsupportedOperationException.class, () -> parsed.put("fail", ""));
    }

    @Test
    void doNotFailOnUnknownProperties() throws JsonProcessingException {
        var mapper = MoreJackson.getObjectMapper();
        mapper.readValue("{\"foobar\":\"\"}", ListWrapper.class);
    }

    @Test
    void parseNullCollectionsToEmpty() throws JsonProcessingException {
        var mapper = MoreJackson.getObjectMapper();
        var parsed = mapper.readValue("{\"strings\":null}", ListWrapper.class);
        assertThat(parsed.strings).isNotNull();
        assertThat(parsed.strings.isEmpty()).isTrue();
        assertThrows(UnsupportedOperationException.class, () -> parsed.strings.add(List.of()));
    }

    @Test
    void parseNullCollectionContentToEmpty() throws JsonProcessingException {
        var mapper = MoreJackson.getObjectMapper();
        var parsed = mapper.readValue("{\"strings\":[null]}", ListWrapper.class);
        assertThat(parsed.strings).isNotNull();
        assertThat(parsed.strings.size()).isEqualTo(1);
        assertThat(parsed.strings.contains(List.of())).isTrue();
        assertThrows(UnsupportedOperationException.class, () -> parsed.strings.add(List.of()));
        assertThrows(UnsupportedOperationException.class, () -> parsed.strings.get(0).add(""));
    }

    @Test
    void useLombokDefaultIfAbsent() throws JsonProcessingException {
        var mapper = MoreJackson.getObjectMapper();
        var parsed = mapper.readValue("{}", ListWrapper.class);
        assertThat(parsed.strings).isNotNull();
        assertThat(parsed.strings.isEmpty()).isTrue();
        assertThrows(UnsupportedOperationException.class, () -> parsed.strings.add(List.of()));
    }

    @Test
    void handleOptional() throws JsonProcessingException {
        var mapper = MoreJackson.getObjectMapper();
        var parsedAbsent = mapper.readValue("{}", OptionalWrapper.class);
        assertThat(parsedAbsent.string).isNotNull();
        assertThat(parsedAbsent.string).isEmpty();
        var parsedNull = mapper.readValue("{\"string\":null}", OptionalWrapper.class);
        assertThat(parsedNull.string).isNotNull();
        assertThat(parsedNull.string).isEmpty();
        var parsedEmpty = mapper.readValue("{\"string\":\"\"}", OptionalWrapper.class);
        assertThat(parsedEmpty.string).isNotNull();
        assertThat(parsedEmpty.string).isEmpty();
        var parsedValued = mapper.readValue("{\"string\":\"foobar\"}", OptionalWrapper.class);
        assertThat(parsedValued.string).isNotNull();
        assertThat(parsedValued.string).isEqualTo(Optional.of("foobar"));
    }

    @Value
    @Builder
    @Jacksonized
    static class ListWrapper {
        @Builder.Default
        List<List<String>> strings = List.of();
    }

    @Value
    @Builder
    @Jacksonized
    static class OptionalWrapper {
        @Builder.Default
        Optional<String> string = Optional.empty();
    }

}
