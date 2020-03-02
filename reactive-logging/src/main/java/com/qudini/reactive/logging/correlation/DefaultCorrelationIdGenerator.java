package com.qudini.reactive.logging.correlation;

import lombok.RequiredArgsConstructor;

import java.math.BigInteger;
import java.security.SecureRandom;

import static java.lang.Long.toHexString;
import static java.time.Instant.now;

/**
 * Largely inspired from <a href="https://github.com/aws/aws-xray-sdk-java/blob/master/aws-xray-recorder-sdk-core/src/main/java/com/amazonaws/xray/entities/TraceID.java">AWS TraceId</a>.
 */
@RequiredArgsConstructor
public final class DefaultCorrelationIdGenerator implements CorrelationIdGenerator {

    private static final class LocalSecureRandom extends ThreadLocal<SecureRandom> {

        @Override
        protected SecureRandom initialValue() {
            return new SecureRandom();
        }

    }

    private static final LocalSecureRandom CURRENT_RANDOM = new LocalSecureRandom();

    private static final int VERSION = 1;

    private static final String CORRELATION_ID_FORMAT = "%s%d-%s-%s";

    private final String prefix;

    @Override
    public String generate() {
        var startTime = now().getEpochSecond();
        var number = new BigInteger(96, CURRENT_RANDOM.get());
        var paddedNumber = new StringBuilder(number.toString(16));
        while (paddedNumber.length() < 24) {
            paddedNumber.insert(0, '0');
        }
        return String.format(
                CORRELATION_ID_FORMAT,
                prefix,
                VERSION,
                toHexString(startTime),
                paddedNumber
        );
    }

}
