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
        var payload = "{  \n" +
                "   \"Records\":[  \n" +
                "      {  \n" +
                "         \"eventVersion\":\"2.1\",\n" +
                "         \"eventSource\":\"aws:s3\",\n" +
                "         \"awsRegion\":\"us-west-2\",\n" +
                "         \"eventTime\":\"1970-01-01T00:00:00.000Z\",\n" +
                "         \"eventName\":\"ObjectCreated:Put\",\n" +
                "         \"userIdentity\":{  \n" +
                "            \"principalId\":\"AIDAJDPLRKLG7UEXAMPLE\"\n" +
                "         },\n" +
                "         \"requestParameters\":{  \n" +
                "            \"sourceIPAddress\":\"127.0.0.1\"\n" +
                "         },\n" +
                "         \"responseElements\":{  \n" +
                "            \"x-amz-request-id\":\"C3D13FE58DE4C810\",\n" +
                "            \"x-amz-id-2\":\"FMyUVURIY8/IgAtTv8xRjskZQpcIZ9KG4V5Wp6S7S/JRWeUWerMUE5JgHvANOjpD\"\n" +
                "         },\n" +
                "         \"s3\":{  \n" +
                "            \"s3SchemaVersion\":\"1.0\",\n" +
                "            \"configurationId\":\"testConfigRule\",\n" +
                "            \"bucket\":{  \n" +
                "               \"name\":\"mybucket\",\n" +
                "               \"ownerIdentity\":{  \n" +
                "                  \"principalId\":\"A3NL1KOZZKExample\"\n" +
                "               },\n" +
                "               \"arn\":\"arn:aws:s3:::mybucket\"\n" +
                "            },\n" +
                "            \"object\":{  \n" +
                "               \"key\":\"HappyFace.jpg\",\n" +
                "               \"size\":1024,\n" +
                "               \"eTag\":\"d41d8cd98f00b204e9800998ecf8427e\",\n" +
                "               \"versionId\":\"096fKKXTRTtl3on89fVO.nfljtsv6qko\",\n" +
                "               \"sequencer\":\"0055AED6DCD90281E5\"\n" +
                "            }\n" +
                "         }\n" +
                "      }\n" +
                "   ]\n" +
                "}";
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
