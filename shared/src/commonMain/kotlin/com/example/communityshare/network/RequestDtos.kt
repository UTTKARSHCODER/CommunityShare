package com.example.communityshare.network

import kotlinx.serialization.Serializable

@Serializable
data class BorrowRequestDto(
    val requestId: String,
    val itemId: String,
    val itemTitle: String,
    val status: String,
    val returnBy: String
)

@Serializable
data class SyncRequestsPayload(
    val requests: List<BorrowRequestDto>
)