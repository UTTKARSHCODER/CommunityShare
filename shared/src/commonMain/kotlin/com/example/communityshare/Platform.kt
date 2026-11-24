package com.example.communityshare

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform