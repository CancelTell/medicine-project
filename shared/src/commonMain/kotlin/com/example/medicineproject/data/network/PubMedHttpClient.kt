package com.example.medicineproject.data.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun pubMedHttpClient(baseUrl: String): HttpClient = HttpClient(createHttpEngine()) {
    expectSuccess = true

    install(HttpTimeout) {
        requestTimeoutMillis = 20_000
    }

    defaultRequest {
        url(baseUrl)
    }

    install(Logging) {
        logger = Logger.SIMPLE
        level = LogLevel.INFO
    }

    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}
