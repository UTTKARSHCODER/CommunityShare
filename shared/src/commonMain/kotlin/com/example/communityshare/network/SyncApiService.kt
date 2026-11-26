package com.example.communityshare.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createApiClient(): HttpClient = HttpClient {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            }
        )
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 15_000
        connectTimeoutMillis = 10_000
        socketTimeoutMillis = 15_000
    }
    install(Logging) {
        level = LogLevel.INFO
    }
    defaultRequest {
        contentType(ContentType.Application.Json)
    }
}

class SyncApiService (
    private val client: HttpClient,
    private val baseUrl: String = "https://api.communityshare.example.com"
) {
    suspend fun pushPendingRequest(requests: List<BorrowRequestDto>): Boolean {
        val response = client.post("$baseUrl/v1/requests/sync") {
            setBody(SyncRequestsPayload(requests = requests))
        }
        return response.status.value in 200..299
    }

    suspend fun fetchRemoteRequests(): List<BorrowRequestDto> {
        return client.get("$baseUrl/v1/resquests").body()
    }
}
