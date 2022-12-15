package com.example.demo

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.services.s3.model.S3ObjectInputStream
import com.example.demo.util.Credentials
import com.example.demo.util.MinioTestContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.reactive.function.client.WebClient
import org.testcontainers.junit.jupiter.Testcontainers

private const val bucketName = "bucket-test"

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Testcontainers
class DemoApplicationTests {

	private val accessKey = "accessKey"
	private val secretKey = "secretKey"

	private val name: String = Regions.US_EAST_1.getName()

	private val endpoint: AwsClientBuilder.EndpointConfiguration =
		AwsClientBuilder.EndpointConfiguration("http://localhost:19100", name)

	private val s3Client = AmazonS3ClientBuilder.standard()
		.withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
		.withEndpointConfiguration(endpoint)
		.withPathStyleAccessEnabled(true)
		.build()

	@Test
	fun simpleTest() {

		createMinioContainer()
		initializeS3Bucket()

		val key = 159
		triggerUpload(key)

		val s3object: S3Object = s3Client.getObject(bucketName, "${key}.txt")
		val inputStream: S3ObjectInputStream = s3object.objectContent
		assertThat(String(inputStream.readAllBytes())).isEqualTo("$key - content:)")

	}

	fun triggerUpload(key: Int): ResponseEntity<String>? {
		return WebClient
			.builder()
			.build()
			.post()
			.uri("http://localhost:8086/test/$key")
			.retrieve()
			.toEntity(String::class.java)
			.block()
	}

	private fun createMinioContainer() {
		val container = MinioTestContainer(Credentials(accessKey, secretKey))
		container.start()
	}

	/**
	 * NOTES - we're still not sure, if minio will work properly, this is a small prepare for AWS in test scope.
	 */
	private fun initializeS3Bucket() {
		s3Client.createBucket(bucketName)
	}

}
