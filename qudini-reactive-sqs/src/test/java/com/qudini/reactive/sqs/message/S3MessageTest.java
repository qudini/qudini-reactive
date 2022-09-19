package com.qudini.reactive.sqs.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("S3Message")
class S3MessageTest {

    @Test
    @DisplayName("should be parsable by Jackson")
    void parse() throws Exception {
        // example copied as is from https://docs.aws.amazon.com/AmazonS3/latest/dev/notification-content-structure.html:
        var payload = """
                {
                  "Records": [
                    {
                      "eventVersion": "2.1",
                      "eventSource": "aws:s3",
                      "awsRegion": "us-west-2",
                      "eventTime": "1970-01-01T00:00:00.000Z",
                      "eventName": "ObjectCreated:Put",
                      "userIdentity": {
                        "principalId": "AIDAJDPLRKLG7UEXAMPLE"
                      },
                      "requestParameters": {
                        "sourceIPAddress": "127.0.0.1"
                      },
                      "responseElements": {
                        "x-amz-request-id": "C3D13FE58DE4C810",
                        "x-amz-id-2": "FMyUVURIY8/IgAtTv8xRjskZQpcIZ9KG4V5Wp6S7S/JRWeUWerMUE5JgHvANOjpD"
                      },
                      "s3": {
                        "s3SchemaVersion": "1.0",
                        "configurationId": "testConfigRule",
                        "bucket": {
                          "name": "mybucket",
                          "ownerIdentity": {
                            "principalId": "A3NL1KOZZKExample"
                          },
                          "arn": "arn:aws:s3:::mybucket"
                        },
                        "object": {
                          "key": "HappyFace.jpg",
                          "size": 1024,
                          "eTag": "d41d8cd98f00b204e9800998ecf8427e",
                          "versionId": "096fKKXTRTtl3on89fVO.nfljtsv6qko",
                          "sequencer": "0055AED6DCD90281E5"
                        }
                      }
                    }
                  ]
                }""";
        var s3Message = new ObjectMapper().readValue(payload, S3Message.class);
        assertThat(s3Message).isNotNull();
        assertThat(s3Message.getRecords()).hasSize(1);
        var record = s3Message.getRecords().get(0);
        assertThat(record.getEventVersion()).isEqualTo("2.1");
        assertThat(record.getEventSource()).isEqualTo("aws:s3");
        assertThat(record.getAwsRegion()).isEqualTo("us-west-2");
        assertThat(record.getEventTime()).isEqualTo("1970-01-01T00:00:00.000Z");
        assertThat(record.getEventName()).isEqualTo("ObjectCreated:Put");
        assertThat(record.getUserIdentity().getPrincipalId()).isEqualTo("AIDAJDPLRKLG7UEXAMPLE");
        assertThat(record.getRequestParameters().getSourceIPAddress()).isEqualTo("127.0.0.1");
        assertThat(record.getResponseElements().getXAmzRequestId()).isEqualTo("C3D13FE58DE4C810");
        assertThat(record.getResponseElements().getXAmzId2()).isEqualTo("FMyUVURIY8/IgAtTv8xRjskZQpcIZ9KG4V5Wp6S7S/JRWeUWerMUE5JgHvANOjpD");
        assertThat(record.getS3().getS3SchemaVersion()).isEqualTo("1.0");
        assertThat(record.getS3().getConfigurationId()).isEqualTo("testConfigRule");
        assertThat(record.getS3().getBucket().getName()).isEqualTo("mybucket");
        assertThat(record.getS3().getBucket().getOwnerIdentity().getPrincipalId()).isEqualTo("A3NL1KOZZKExample");
        assertThat(record.getS3().getBucket().getArn()).isEqualTo("arn:aws:s3:::mybucket");
        assertThat(record.getS3().getObject().getKey()).isEqualTo("HappyFace.jpg");
        assertThat(record.getS3().getObject().getSize()).isEqualTo(1024);
        assertThat(record.getS3().getObject().getETag()).isEqualTo("d41d8cd98f00b204e9800998ecf8427e");
        assertThat(record.getS3().getObject().getVersionId()).isEqualTo("096fKKXTRTtl3on89fVO.nfljtsv6qko");
        assertThat(record.getS3().getObject().getSequencer()).isEqualTo("0055AED6DCD90281E5");
    }

}
