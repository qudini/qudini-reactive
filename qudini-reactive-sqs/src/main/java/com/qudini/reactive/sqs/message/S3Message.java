package com.qudini.reactive.sqs.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * See https://docs.aws.amazon.com/AmazonS3/latest/dev/notification-content-structure.html.
 */
@Value
@Builder
@NoArgsConstructor(force = true, access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class S3Message {

    @Value
    @Builder
    @NoArgsConstructor(force = true, access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    public static class Record {

        @Value
        @Builder
        @NoArgsConstructor(force = true, access = PRIVATE)
        @AllArgsConstructor(access = PRIVATE)
        public static class UserIdentity {

            String principalId;

        }

        @Value
        @Builder
        @NoArgsConstructor(force = true, access = PRIVATE)
        @AllArgsConstructor(access = PRIVATE)
        public static class RequestParameters {

            String sourceIPAddress;

        }

        @Value
        @Builder
        @NoArgsConstructor(force = true, access = PRIVATE)
        @AllArgsConstructor(access = PRIVATE)
        public static class ResponseElements {

            @JsonProperty("x-amz-request-id")
            String xAmzRequestId;

            @JsonProperty("x-amz-id-2")
            String xAmzId2;

        }

        @Value
        @Builder
        @NoArgsConstructor(force = true, access = PRIVATE)
        @AllArgsConstructor(access = PRIVATE)
        public static class S3 {

            @Value
            @Builder
            @NoArgsConstructor(force = true, access = PRIVATE)
            @AllArgsConstructor(access = PRIVATE)
            public static class Bucket {

                @Value
                @Builder
                @NoArgsConstructor(force = true, access = PRIVATE)
                @AllArgsConstructor(access = PRIVATE)
                public static class OwnerIdentity {

                    String principalId;

                }

                String name;

                OwnerIdentity ownerIdentity;

                String arn;

            }

            @Value
            @Builder
            @NoArgsConstructor(force = true, access = PRIVATE)
            @AllArgsConstructor(access = PRIVATE)
            public static class Object {

                String key;

                long size;

                @JsonProperty("eTag")
                String eTag;

                String versionId;

                String sequencer;

            }

            String s3SchemaVersion;

            String configurationId;

            Bucket bucket;

            Object object;

        }

        String eventVersion;

        String eventSource;

        String awsRegion;

        String eventTime;

        String eventName;

        UserIdentity userIdentity;

        RequestParameters requestParameters;

        ResponseElements responseElements;

        S3 s3;

    }

    @JsonProperty("Records")
    List<Record> records;

}
