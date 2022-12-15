package com.example.demo.controller

import com.example.demo.service.OperationStatus
import com.example.demo.service.S3StorageService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.MimeTypeUtils
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
class S3Controller(
    private val storageService: S3StorageService,
) {

    @PostMapping("/test/{id}", produces = [MimeTypeUtils.APPLICATION_JSON_VALUE])
    fun exportConsole(
        @PathVariable id: Long,
    ): ResponseEntity<String> {

        val operationResult = storageService.storeFile(id)

        return ResponseEntity
            .status(mapStatus(operationResult.status))
            .body(operationResult.message)
    }

    companion object {
        private fun mapStatus(status: OperationStatus): HttpStatus {
            return when (status) {
                OperationStatus.OK -> HttpStatus.OK
                OperationStatus.FAIL -> HttpStatus.INTERNAL_SERVER_ERROR
            }
        }
    }
}