package com.qudini.reactive.utils.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.base.Strings;

import java.io.IOException;
import java.io.Serial;

public final class EmptyToNullStringDeserializer extends StdDeserializer<String> {

    @Serial
    private static final long serialVersionUID = 1L;

    public EmptyToNullStringDeserializer() {
        super(String.class);
    }

    @Override
    public String deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return Strings.emptyToNull(parser.getValueAsString());
    }

}
