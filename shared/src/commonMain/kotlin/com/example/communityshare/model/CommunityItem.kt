package com.example.communityshare.model

data class CommunityItem(
    val id: String,
    val title: String,
    val category: String,
    val distanceKm: Double,
    val rating: Double,
    val ownerName: String,
    val description: String,
    val pickupRules: String
)

val sampleItems = listOf(
    CommunityItem(
        id = "1",
        title = "Electric Drill",
        category = "Tools",
        distanceKm = 0.3,
        rating = 4.8,
        ownerName = "Alex R.",
        description = "Bosch 18V cordless drill with battery pack and charger.",
        pickupRules = "Return cleaned by 8 PM"
    ),
    CommunityItem(
        id = "2",
        title = "Stand Mixer",
        category = "Kitchen",
        distanceKm = 0.3,
        rating = 4.8,
        ownerName = "Priya M.",
        description = "Heavy-duty kitchen stand mixer with dough hook and whisk.",
        pickupRules = "Wipe down bowl before return"
    ),
    CommunityItem(
        id = "3",
        title = "Folding Ladder",
        category = "Tools",
        distanceKm = 0.4,
        rating = 4.9,
        ownerName = "Rohan S.",
        description = "6-foot lightweight aluminium step ladder.",
        pickupRules = "Pickup from ground floor lobby"
    ),
)