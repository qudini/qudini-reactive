# qudini-reactive

Libraries simplifying the development of applications based on the reactive stack of Spring: WebFlux with Project Reactor.

They are all preconfigured, so that just adding them as dependencies is enough to get started. Further fine-grained configuration is available if needed.

## Installation

Add a Maven repository that points to `qudini-reactive` GitHub Packages:

```xml
<repositories>
    <repository>
      <id>qudini-reactive</id>
      <url>https://maven.pkg.github.com/qudini/qudini-reactive</url>
    </repository>
</repositories>
```

## Libraries:

- [`qudini-reactive-utils`](./qudini-reactive-utils/)
- [`qudini-reactive-logging`](./qudini-reactive-logging/)
- [`qudini-reactive-metrics`](./qudini-reactive-metrics/)
- [`qudini-reactive-sqs`](./qudini-reactive-sqs/)
