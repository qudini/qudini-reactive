package com.qudini.reactive.graphql.app;

import com.qudini.gom.Arguments;
import com.qudini.gom.FieldResolver;
import com.qudini.gom.TypeResolver;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@TypeResolver("Query")
public class QueryResolver {

    @FieldResolver("myFooBars")
    public List<MyFooBar> myFooBars() {
        return List.of(new MyFoo(), new MyBar());
    }

    @FieldResolver("myBoolean")
    public String myBoolean(Arguments arguments) {
        return arguments.<ScalarValue>get("in").name();
    }

    @FieldResolver("myString")
    public String myString(Arguments arguments) {
        return arguments.<ScalarValue>get("in").name();
    }

    @FieldResolver("myInt")
    public String myInt(Arguments arguments) {
        return arguments.<ScalarValue>get("in").name();
    }

    @FieldResolver("myFloat")
    public String myFloat(Arguments arguments) {
        return arguments.<ScalarValue>get("in").name();
    }

    @FieldResolver("context")
    public Mono<String> context() {
        return Mono.deferContextual(Mono::just).map(context -> context.get(String.class));
    }

}
