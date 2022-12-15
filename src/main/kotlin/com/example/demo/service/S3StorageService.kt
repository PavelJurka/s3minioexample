package com.example.demo.service

import com.amazonaws.services.s3.AmazonS3
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.IOException
import java.nio.ByteBuffer
import java.util.Random


@Service
class S3StorageService(
    private val s3Client: AmazonS3
) {

    @Value("\${application.minio.bucket.name}")
    var bucketName: String? = null

    fun storeFile(id: Long): OperationResult {
        return try {
            s3Client.putObject(bucketName, "${id}.txt", "$id - content:)")
            OperationResult(OperationStatus.OK, "success")
        } catch (e: Exception) {
            OperationResult(OperationStatus.FAIL, e.message ?: "Unknown error")
        }
    }
}

enum class OperationStatus {
    OK,
    FAIL
}

data class OperationResult(
    val status: OperationStatus,
    val message: String
)