package com.crm.sales.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * A configuration class that provides a bean for the Amazon S3 client.
 * The region for the Amazon S3 client is set using the value from the application.properties file.
 * </p>
 *
 * @author Gokul Annadurai
 * @since Dec 23, 2024
 */
@Configuration
public class AwsConfig {

    @Value("${cloud.aws.region}")
    private String awsRegion;

    @Value("${cloud.aws.access-key}")
    private String awsAccessKeyId;

    @Value("${cloud.aws.secret-access-key}")
    private String awsSecretKey;

    /**
     * Generates an Amazon S3 client based on the environment.
     * For EKS, it uses WebIdentityTokenCredentialsProvider.
     * For EC2, it uses region from the application properties.
     *
     * @return The Amazon S3 client.
     */
    @Bean
    public AmazonS3 amazonS3Client() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretKey);
        return AmazonS3ClientBuilder.standard().withRegion(awsRegion).withCredentials(
                        new AWSStaticCredentialsProvider(awsCredentials))
                .build();

    }
}

