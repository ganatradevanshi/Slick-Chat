package com.neu.prattle.service.mediaservice;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.neu.prattle.utils.ConfigUtils;

import java.io.ByteArrayInputStream;

/**
 * AWS Implementation of the MediaService, all user upload files go to a S3 Bucket.
 */
public class AWSMediaService implements MediaService {

    private static AWSMediaService service;
    private AmazonS3 s3Client;
    private String bucketName;

    /**
     * Setup AmazonS3Client using the credentials from the active profile.
     */
    private AWSMediaService() {
        ConfigUtils utils = ConfigUtils.getInstance();
        bucketName = utils.getPropertyValue("aws_bucket_name");

        AWSCredentials credentials =
                new BasicAWSCredentials(utils.getPropertyValue("aws_access_key"),
                        utils.getPropertyValue("aws_secret_key"));

        s3Client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_2)
                .build();
    }

    public static AWSMediaService getInstance() {
        if (service == null) {
            service = new AWSMediaService();
        }
        return service;
    }

    @Override
    public String upload(byte[] bytes, String fileName) {
        s3Client.putObject(bucketName, fileName, new ByteArrayInputStream(bytes), new ObjectMetadata());
        return s3Client.getUrl(bucketName, fileName).toString();
    }

    @Override
    public void delete(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
    }

    @Override
    public boolean mediaExists(String fileName) {
        try {
            s3Client.getObject(bucketName, fileName);
        } catch (AmazonS3Exception e) {
            return false;
        }
        return true;
    }
}
