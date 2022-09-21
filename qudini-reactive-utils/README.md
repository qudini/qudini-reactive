# qudini-reactive-utils

See also [`qudini-utils`](https://github.com/qudini/qudini-utils).

## Installation

```xml
<dependencies>
    <dependency>
        <groupId>com.qudini</groupId>
        <artifactId>qudini-reactive-utils</artifactId>
    </dependency>
</dependencies>
```

## Configuration

You can leave the defaults, everything will just work out of the box. You can also reconfigure it to match your requirements, as explained in the following sections.

### Metadata

You can inject `com.qudini.reactive.utils.metadata.MetadataService` to access metadata about the artifact and the environment. By default:

- the environment will be read from the environment variable `K8S_NAMESPACE`,
- the build name will be read from the JAR file's manifest `Implementation-Title`,
- the build version will be read from the JAR file's manifest `Implementation-Version`.

You can override this behaviour by registering a component implementing `com.qudini.reactive.utils.metadata.MetadataService`.

## Usage

### com.qudini.reactive.utils.MoreWebClients

- `#newWebClient()`: returns a new preconfigured `WebClient`
