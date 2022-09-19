package com.qudini.reactive.utils.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;

import java.io.IOException;
import java.io.Serial;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public final class UnmodifiableCollectionsDeserializerModifier extends BeanDeserializerModifier {

    @Override
    public JsonDeserializer<?> modifyCollectionDeserializer(DeserializationConfig config, CollectionType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        if (List.class.isAssignableFrom(type.getRawClass())) {
            return new UnmodifiableCollectionDeserializer<List<?>>(deserializer, Collections::unmodifiableList, Collections::emptyList);
        } else if (Set.class.isAssignableFrom(type.getRawClass())) {
            return new UnmodifiableCollectionDeserializer<Set<?>>(deserializer, Collections::unmodifiableSet, Collections::emptySet);
        } else {
            return deserializer;
        }
    }

    @Override
    public JsonDeserializer<?> modifyMapDeserializer(DeserializationConfig config, MapType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        if (Map.class.isAssignableFrom(type.getRawClass())) {
            return new UnmodifiableCollectionDeserializer<Map<?, ?>>(deserializer, Collections::unmodifiableMap, Collections::emptyMap);
        } else {
            return deserializer;
        }
    }

    private static final class UnmodifiableCollectionDeserializer<T> extends DelegatingDeserializer {

        @Serial
        private static final long serialVersionUID = 1L;

        private final Function<T, T> unmodifier;
        private final Supplier<T> emptySupplier;

        public UnmodifiableCollectionDeserializer(JsonDeserializer<?> delegate, Function<T, T> unmodifier, Supplier<T> emptySupplier) {
            super(delegate);
            this.unmodifier = unmodifier;
            this.emptySupplier = emptySupplier;
        }

        @Override
        protected JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> delegate) {
            return new UnmodifiableCollectionDeserializer<>(delegate, unmodifier, emptySupplier);
        }

        @Override
        public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            var value = super.deserialize(parser, context);
            return value == null ? getNullValue(context) : unmodifier.apply((T) value);
        }

        @Override
        public Object getNullValue(DeserializationContext context) {
            return emptySupplier.get();
        }

        @Override
        public Object getEmptyValue(DeserializationContext context) {
            return getNullValue(context);
        }

        @Override
        public Object getAbsentValue(DeserializationContext context) {
            return getNullValue(context);
        }

    }

}
