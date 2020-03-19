# qudini-reactive-utils

Utilities around Project Reactor.

## Installation

```xml
<dependencies>
    <dependency>
        <groupId>com.qudini</groupId>
        <artifactId>qudini-reactive-utils</artifactId>
        <version>${qudini-reactive.version}</version>
    </dependency>
</dependencies>
```

## Usage

### com.qudini.reactive.utils.MoreMonos

Utilities around monos.

```java
Mono<Optional<T>> example(Mono<T> mono) {
    return mono.transform(MoreMonos::toOptional);
}
```

### com.qudini.reactive.utils.MoreFunctions

Utilities around functions.

#### Throwable functions

```java
Consumer<T> example() {
    return throwableConsumer(x -> {
        throw new Exception();
    });
}

Function<T, R> example() {
    return throwableFunction(x -> {
        throw new Exception();
    });
}

Supplier<R> example() {
    return throwableSupplier(() -> {
        throw new Exception();
    });
}

Predicate<T> example() {
    return throwablePredicate(x -> {
        throw new Exception();
    });
}

BiConsumer<T1, T2> example() {
    return throwableBiConsumer((x, y) -> {
        throw new Exception();
    });
}

BiFunction<T1, T2, R> example() {
    return throwableBiFunction((x, y) -> {
        throw new Exception();
    });
}

BiPredicate<T1, T2> example() {
    return throwableBiPredicate((x, y) -> {
        throw new Exception();
    });
}
```

### com.qudini.reactive.utils.MoreTuples

Utilities around tuples.

#### Building

```java
Flux<Tuple2<T1, T2>> example(Mono<Map<T1, T2>> map) {
    return map
            .flatMapIterable(Map::entrySet)
            .map(MoreTuples::fromEntry);
}

Tuple2<Integer, Integer> example() {
    return MoreTuples.fromArray(new Integer[]{1, 2});
}
```

#### Reducing

```java
Mono<Integer> example(Mono<Tuple2<Integer, String>> mono) {
    return mono.map(MoreTuples::left);
}

Mono<String> example(Mono<Tuple2<Integer, String>> mono) {
    return mono.map(MoreTuples::right);
}

Mono<String> example(Mono<Tuple2<Integer, String>> mono) {
    return mono.map(onBoth((i, s) -> s + i));
}

Mono<String> example(Mono<Tuple2<Integer, String>> mono) {
    return mono.flatMap(onBoth((i, s) -> Mono.just(s + i)));
}
```

#### Mapping

```java
Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
    return mono.map(onLeft(i -> i + 1));
}

Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
    return mono.flatMap(onLeftWhen(i -> Mono.just(i + 1)));
}

Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
    return mono.map(onRight(s -> s + "bar"));
}

Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
    return mono.flatMap(onRightWhen(s -> Mono.just(s + "bar")));
}

Mono<Tuple2<String, String>> example(Mono<Tuple2<Integer, Integer>> mono) {
    return mono.map(onEach(Object::toString));
}
```

#### Filtering

```java
Mono<Tuple2<Integer, Integer>> example(Mono<Tuple2<Integer, Integer>> mono) {
    return mono.filter(ifEach(i -> 0 < i));
}

Mono<Tuple2<Integer, Integer>> example(Mono<Tuple2<Integer, Integer>> mono) {
    return mono.filter(ifEither(i -> 0 < i));
}

Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
    return mono.filter(ifLeft(i -> 0 < i));
}

Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
    return mono.filter(ifRight("foobar"::equals));
}

Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
    return mono.filter(ifBoth((i, s) -> "foo42".equals(s + i)));
}

Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
    return mono.filterWhen(fromLeft(i -> Mono.just(0 < i)));
}

Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
    return mono.filterWhen(fromRight(s -> Mono.just("foobar".equals(s))));
}

Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
    return mono.filterWhen(onBoth((i, s) -> Mono.just("foo42".equals(s + i))));
}
```

#### Consuming

```java
Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
    return mono.doOnNext(takeLeft(i -> log.debug("i:{}", i)));
}

Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
    return mono.doOnNext(takeRight(s -> log.debug("s:{}", s)));
}

Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
    return mono.doOnNext(takeBoth((i, s) -> log.debug("i:{} s:{}", i, s)));
}
```
