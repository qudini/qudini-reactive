# qudini-reactive

Spring WebFlux helps building scalable applications, Qudini Reactive helps making them production-ready.

All these libraries are preconfigured: just adding them as dependencies is enough to get started. Further fine-grained configuration is available if needed.

## Installation

Most of the libraries need the following dependencies to be provided:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

## Libraries:

- [`qudini-reactive-utils`](./qudini-reactive-utils/)
- [`qudini-reactive-logging`](./qudini-reactive-logging/)
- [`qudini-reactive-metrics`](./qudini-reactive-metrics/)
- [`qudini-reactive-sqs`](./qudini-reactive-sqs/)
- [`qudini-reactive-graphql`](./qudini-reactive-graphql/)
