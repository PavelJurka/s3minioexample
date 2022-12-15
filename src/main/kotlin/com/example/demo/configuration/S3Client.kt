package com.example.demo.configuration

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class S3Client {
    @Value("\${application.minio.access.name}")
    var accessKey: String? = null

    @Value("\${application.minio.access.secret}")
    var secretKey: String? = null

    @Value("\${application.minio.url}")
    var url: String? = null

    @Value("\${application.minio.port}")
    var port: Int = 0

    @Bean
    fun generateClient(): AmazonS3 {
        val name: String = Regions.US_EAST_1.getName()
        val endpoint: AwsClientBuilder.EndpointConfiguration =
            AwsClientBuilder.EndpointConfiguration("$url:$port", name)

        return AmazonS3ClientBuilder.standard()
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
            .withEndpointConfiguration(endpoint)
            .withPathStyleAccessEnabled(true)
            .build()
    }
}