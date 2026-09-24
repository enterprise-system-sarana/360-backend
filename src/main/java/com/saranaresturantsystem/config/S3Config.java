package com.saranaresturantsystem.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.net.URI;
import java.util.Arrays;

@Configuration
public class S3Config {
    private static final Logger log = LoggerFactory.getLogger(S3Config.class);

    @Value("${rustfs.endpoint}")
    private String endpoint;

    @Value("${rustfs.access-key}")
    private String accessKey;

    @Value("${rustfs.secret-key}")
    private String secretKey;

    @Value("${aws.region:us-east-1}")
    private String region;

    @Value("${rustfs.buckets:${rustfs.bucket}}")
    private String buckets;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey,secretKey))) 
                .serviceConfiguration( S3Configuration.builder() 
                .pathStyleAccessEnabled(true) 
                .build())
                .build();
    }

    @Bean
    public ApplicationRunner s3BucketInitializer(S3Client s3Client) {
        return args -> {
            for (String bucket : Arrays.stream(buckets.split(","))
                    .map(String::trim)
                    .filter(name -> !name.isEmpty())
                    .distinct()
                    .toList()) {
                try {
                    s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
                    log.info("S3 bucket '{}' is already available", bucket);
                } catch (S3Exception exception) {
                    if (exception.statusCode() != 404) {
                        throw new IllegalStateException(
                                "Unable to access S3 bucket '" + bucket + "'", exception);
                    }

                    try {
                        s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
                        log.info("Created S3 bucket '{}'", bucket);
                    } catch (BucketAlreadyExistsException | BucketAlreadyOwnedByYouException alreadyExists) {
                        log.info("S3 bucket '{}' already exists", bucket);
                    }
                }
            }
        };
    }
}