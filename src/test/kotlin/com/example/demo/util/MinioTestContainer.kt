package com.example.demo.util

import org.testcontainers.containers.FixedHostPortGenericContainer
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy
import org.testcontainers.utility.Base58
import java.time.Duration


class MinioTestContainer(image: String, credentials: Credentials) : FixedHostPortGenericContainer<MinioTestContainer>(image) {

    init {
        withNetworkAliases("minio-" + Base58.randomString(6))
        addExposedPort(DEFAULT_PORT)
        addExposedPort(DEFAULT_CONSOLE_PORT)

        withFixedExposedPort(EXPOSED_PORT, DEFAULT_PORT)
        withFixedExposedPort(EXPOSED_CONSOLE_PORT, DEFAULT_CONSOLE_PORT)
        withCommand("server", DEFAULT_STORAGE_DIRECTORY)
        withEnv(MINIO_ACCESS_KEY, credentials.accessKey)
        withEnv(MINIO_SECRET_KEY, credentials.secretKey)
        withCommand("server /data --console-address :$DEFAULT_CONSOLE_PORT")
        setWaitStrategy(
            HttpWaitStrategy()
                .forPort(DEFAULT_PORT)
                .forPath(HEALTH_ENDPOINT)
                .withStartupTimeout(Duration.ofMinutes(2))
        )
    }

    constructor(credentials: Credentials) : this(
        image = "$DEFAULT_IMAGE",
        credentials = credentials
    )

    companion object {
        private const val DEFAULT_IMAGE = "minio/minio"
        private const val DEFAULT_PORT = 9000
        private const val DEFAULT_CONSOLE_PORT = 9001
        private const val EXPOSED_PORT = 19100
        private const val EXPOSED_CONSOLE_PORT = 19101
        private const val DEFAULT_STORAGE_DIRECTORY = "/data"
        private const val HEALTH_ENDPOINT = "/minio/health/ready"
        private const val MINIO_ACCESS_KEY = "MINIO_ACCESS_KEY"
        private const val MINIO_SECRET_KEY = "MINIO_SECRET_KEY"
    }
}

class Credentials(val accessKey: String, val secretKey: String)
