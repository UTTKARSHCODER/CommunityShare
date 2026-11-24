package com.example.communityshare.db

import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DatabaseDriverFactory): CommunityShareDatabase {
    val driver = driverFactory.createDriver()
    return CommunityShareDatabase(driver)
}