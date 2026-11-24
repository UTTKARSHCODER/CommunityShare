package com.example.communityshare

import androidx.compose.ui.window.ComposeUIViewController
import com.example.communityshare.db.DatabaseDriverFactory

fun MainViewController() = ComposeUIViewController {
    val driverFactory = DatabaseDriverFactory()
    App(driverFactory = driverFactory)
}