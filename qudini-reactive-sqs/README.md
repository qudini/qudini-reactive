# qudini-reactive-sqs

Non-blocking SQS listeners based on AWS asynchronous SDK and long polling. 

## Installation

```xml
<dependencies>
    <!-- Qudini Reactive dependencies needed: -->
    <dependency>
        <groupId>com.qudini</groupId>
        <artifactId>qudini-reactive-utils</artifactId>
        <version>${qudini-reactive.version}</version>
    </dependency>
    <dependency>
        <groupId>com.qudini</groupId>
        <artifactId>qudini-reactive-logging</artifactId>
        <version>${qudini-reactive.version}</version>
    </dependency>
    <!-- Main dependency: -->
    <dependency>
        <groupId>com.qudini</groupId>
        <artifactId>qudini-reactive-sqs</artifactId>
        <version>${qudini-reactive.version}</version>
    </dependency>
</dependencies>
```

## Configuration

You can leave the defaults, everything will just work out of the box. You can also reconfigure it to match your requirements, as explained in the following sections.

### SQS client

A default SQS client will be registered via [`SqsAsyncClient.create()`](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/sqs/SqsAsyncClient.html#create--), but you can provide your own bean if needed.

### ObjectMapper

By default, Spring's ObjectMapper bean will be used to parse the incoming SQS messages. You can override this by providing a bean named `sqsMessageObjectMapper`.

## Usage

Register components implementing `com.qudini.reactive.sqs.SqsListener`:

```java
@Component
public class YourSqsListener implements SqsListener {

    @Override
    public String getQueueName() {
        return "your-sqs-queue";
    }

    @Override
    public Class<T> getMessageType() {
        return YourMessage.class
    }

    @Override
    public Mono<Void> handleMessage(YourMessage message, Acknowledger acknowledger) {
        return Log
            .thenMono(() -> {
                log.info("Received a new SQS message: {}", message);
                return process(message);
            })
            .then(acknowledger.acknowledge());
    }

}
```

Only one listener per queue name is allowed.

You can customise the way the `ReceiveMessageRequest` is built by overriding `#buildReceiveMessageRequest(String queueUrl)`:

```java
@Override
public ReceiveMessageRequest buildReceiveMessageRequest(String queueUrl) {
    return ReceiveMessageRequest.builder()
            .queueUrl(queueUrl)
            .maxNumberOfMessages(10)
            .visibilityTimeout(5)
            .waitTimeSeconds(20)
            .build();
}
```

The above configuration is the default one that will be used if not overridden.

The incoming SQS messages will be parsed with Jackson.

A correlation id per long polling cycle will be generated, see [`qudini-reactive-logging`](../qudini-reactive-logging/).

Acknowledging the message will delete it from SQS, so the recommended approach is to register a [dead-letter queue](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-dead-letter-queues.html) and acknowledge a message only on success.

### S3

If [S3 is sending messages](https://docs.aws.amazon.com/AmazonS3/latest/dev/notification-content-structure.html) to your SQS queue, you can use `com.qudini.reactive.sqs.message.S3Message` as the message type of your listener.
