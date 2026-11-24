package com.example.communityshare.navigation

sealed class Screen(val route: String, val title: String) {
    object Discover: Screen("discover", "Discover")
    object Requests: Screen("requests", "Requests")
    object Profile: Screen("profile", "Profile")
    object ItemDetail: Screen("item_detail", "Item Detail")
    object AddItem: Screen("add_item", "Add Item")
    data class HandOffVerification(val requestId: String, val itemTitle: String) : Screen("handoff", "Handoff Verification")
}