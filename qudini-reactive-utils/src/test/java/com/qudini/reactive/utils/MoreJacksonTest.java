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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
    void doNotSerialiseEmptyArray() throws JsonProcessingException {
        var mapper = MoreJackson.getObjectMapper();
        Map<String, Object> map = new HashMap<>();
        map.put("foobar", new String[0]);
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
    }

    @Test
    void doNotFailOnUnknownProperties() throws JsonProcessingException {
        var mapper = MoreJackson.getObjectMapper();
        mapper.readValue("{\"foobar\":\"\"}", EmptyClass.class);
    }

    @Test
    void parseNullCollectionsToEmpty() throws JsonProcessingException {
        var mapper = MoreJackson.getObjectMapper();
        var parsed = mapper.readValue("{\"strings\":null}", ListWrapper.class);
        assertThat(parsed.strings).isNotNull();
        assertThat(parsed.strings.isEmpty()).isTrue();
    }

    @Test
    void parseNullCollectionContentToEmpty() throws JsonProcessingException {
        var mapper = MoreJackson.getObjectMapper();
        var parsed = mapper.readValue("{\"strings\":[null]}", ListWrapper.class);
        assertThat(parsed.strings).isNotNull();
        assertThat(parsed.strings.size()).isEqualTo(1);
        assertThat(parsed.strings.contains(List.of())).isTrue();
    }

    @Test
    void useLombokDefaultIfAbsent() throws JsonProcessingException {
        var mapper = MoreJackson.getObjectMapper();
        var parsed = mapper.readValue("{}", ListWrapper.class);
        assertThat(parsed.strings).isNotNull();
        assertThat(parsed.strings.isEmpty()).isTrue();
    }

    @Value
    @Builder
    @Jacksonized
    static class EmptyClass {
    }

    @Value
    @Builder
    @Jacksonized
    static class ListWrapper {
        @Builder.Default
        List<List<String>> strings = List.of();
    }

}
