package com.example.communityshare.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.communityshare.db.CommunityShareDatabase
import comexamplecommunityshare.db.BorrowRequestEntity
import comexamplecommunityshare.db.CommunityItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

class ItemRepository(database: CommunityShareDatabase) {
    private val queries = database.communityShareQueries

    // Automatically emits updates whenever items change in SQLite
    fun observeAllItems(): Flow<List<CommunityItemEntity>> {
        return queries.selectAllItems()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    fun observeAllRequests(): Flow<List<BorrowRequestEntity>> {
        return queries.selectAllRequests()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    fun saveItem(item: CommunityItemEntity) {
        queries.insertOrReplaceItem(
            id = item.id,
            title = item.title,
            category = item.category,
            distanceKm = item.distanceKm,
            rating = item.rating,
            ownerName = item.ownerName,
            description = item.description,
            pickupRules = item.pickupRules,
            isAvailable = item.isAvailable
        )
    }

    fun createBorrowRequest(
        requestId: String,
        itemId: String,
        itemTitle: String,
        returnBy: String
    ) {
        queries.insertBorrowRequest(
            requestId = requestId,
            itemId = itemId,
            itemTitle = itemTitle,
            status = "PENDING",
            returnBy = returnBy,
            isSynced = 0L // Marked for background sync when online
        )
    }

    suspend fun seedInitialDataIfEmpty() {
        val existing = queries.selectAllItems().executeAsList()
        if (existing.isEmpty()) {
            val initialItems = listOf(
                CommunityItemEntity(
                    id = "1",
                    title = "Electric Drill",
                    category = "Tools",
                    distanceKm = 0.3,
                    rating = 4.8,
                    ownerName = "Alex R.",
                    description = "Bosch 18V cordless drill with battery pack and charger.",
                    pickupRules = "Return cleaned by 8 PM",
                    isAvailable = 1L
                ),
                CommunityItemEntity(
                    id = "2",
                    title = "Stand Mixer",
                    category = "Kitchen",
                    distanceKm = 0.3,
                    rating = 4.8,
                    ownerName = "Priya M.",
                    description = "Heavy-duty kitchen stand mixer with dough hook and whisk.",
                    pickupRules = "Wipe down bowl before return",
                    isAvailable = 1L
                ),
                CommunityItemEntity(
                    id = "3",
                    title = "Folding Ladder",
                    category = "Tools",
                    distanceKm = 0.4,
                    rating = 4.9,
                    ownerName = "Rohan S.",
                    description = "6-foot lightweight aluminium step ladder.",
                    pickupRules = "Pickup from ground floor lobby",
                    isAvailable = 1L
                )
            )
            initialItems.forEach { saveItem(it) }
        }
    }

    fun cancelBorrowRequest(requestId: String) {
        queries.deleteBorrowRequest(requestId)
    }

    fun updateRequestStatus(requestId: String, newStatus: String) {
        queries.updateRequestStatus(
            status = newStatus,
            requestId = requestId
        )
    }
}
