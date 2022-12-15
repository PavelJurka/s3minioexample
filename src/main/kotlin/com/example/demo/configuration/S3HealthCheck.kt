package com.example.demo.configuration

import com.amazonaws.services.s3.AmazonS3
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component


@Component
class S3HealthCheck(
    private val s3Client: AmazonS3
) : HealthIndicator {

    @Value("\${application.minio.bucket.name}")
    var bucketName: String? = null

    override fun health(): Health {
        return if (!isRunning()) {
            Health.down().withDetail(SERVICE, "Not Available").build()
        } else Health.up().withDetail(SERVICE, "Available").build()
    }

    // Logic Skipped
    private fun isRunning(): Boolean {
        return try {
            val found = s3Client.doesBucketExistV2(bucketName)
            if (found) {
                LOGGER.debug("bucket [$bucketName] looks accessible.")
            } else {
                LOGGER.warn("Unable to find bucket [$bucketName] - health DOWN!")
            }
            found
        } catch (e: Exception) {
            LOGGER.error("Minio health check - DOWN due to [${e.message}]", e)
            false
        }
    }


    companion object {
        const val SERVICE = "Minio"
        private val LOGGER = LoggerFactory.getLogger(this::class.java)
    }
}