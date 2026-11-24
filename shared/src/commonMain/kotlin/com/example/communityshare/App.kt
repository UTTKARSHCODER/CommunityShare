package com.example.communityshare

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.communityshare.data.ItemRepository
import com.example.communityshare.db.DatabaseDriverFactory
import com.example.communityshare.db.createDatabase
import com.example.communityshare.navigation.Screen
import com.example.communityshare.screens.AddItemScreen
import com.example.communityshare.screens.DiscoverScreen
import com.example.communityshare.screens.HandoffScreen
import com.example.communityshare.screens.ItemDetailScreen
import com.example.communityshare.screens.RequestsScreen
import com.example.communityshare.theme.AppColorScheme
import comexamplecommunityshare.db.CommunityItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun App(driverFactory: DatabaseDriverFactory) {
    // Initialize SQLDelight database & repository once
    val database = remember { createDatabase(driverFactory) }
    val repository = remember { ItemRepository(database) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        kotlinx.coroutines.withContext(Dispatchers.IO) {
            repository.seedInitialDataIfEmpty()
        }
    }

    val items by repository.observeAllItems().collectAsState(initial = emptyList())
    val requests by repository.observeAllRequests().collectAsState(initial = emptyList())



    MaterialTheme(colorScheme = AppColorScheme) {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Discover) }
        var selectedItem by remember { mutableStateOf<CommunityItemEntity?>(null) }

        // State to track which request is currently undergoing handoff verification
        var activeHandleOffRequestId by remember { mutableStateOf<String?>(null) }
        var activeHandleOffItemTitle by remember { mutableStateOf<String>("") }

        val navTabs = listOf(
            Screen.Discover,
            Screen.Requests,
            Screen.Profile,
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                // Hide bottom bar on detail and handoff screens for immersive flow
                val hideBottomBar = currentScreen is Screen.ItemDetail ||
                        currentScreen is Screen.AddItem ||
                        currentScreen is Screen.HandOffVerification

                if (!hideBottomBar) {
                    NavigationBar {
                        navTabs.forEach { screen ->
                            val selected = currentScreen == screen
                            NavigationBarItem(
                                icon = {
                                    val iconVector = when (screen) {
                                        Screen.Discover -> Icons.Default.Home
                                        Screen.Requests -> Icons.Default.ListAlt
                                        else -> Icons.Default.Person
                                    }
                                    Icon(iconVector, contentDescription = screen.title)
                                },
                                label = { Text(screen.title) },
                                selected = selected,
                                onClick = { currentScreen = screen }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (val screen = currentScreen) {
                    Screen.Discover -> {
                        DiscoverScreen(
                            items = items,
                            onItemClick = { item ->
                                selectedItem = item
                                currentScreen = Screen.ItemDetail
                            },
                            onAddClick = {
                                currentScreen = Screen.AddItem
                            }
                        )
                    }

                    Screen.AddItem -> {
                        AddItemScreen(
                            onBackClick = { currentScreen = Screen.Discover },
                            onItemSaved = { newItem ->
                                coroutineScope.launch(Dispatchers.IO) {
                                    repository.saveItem(newItem)
                                }
                                currentScreen = Screen.Discover
                            }
                        )
                    }

                    Screen.ItemDetail -> {
                        selectedItem?.let { item ->
                            ItemDetailScreen(
                                item = item,
                                onBackClick = { currentScreen = Screen.Discover },
                                onRequestBorrow = {
                                    coroutineScope.launch(Dispatchers.IO) {
                                        repository.createBorrowRequest(
                                            requestId = "REQ-${Random.nextInt(1000, 9999)}",
                                            itemId = item.id,
                                            itemTitle = item.title,
                                            returnBy = "Friday, 8:00 PM"
                                        )
                                    }
                                    currentScreen = Screen.Requests
                                }
                            )
                        }
                    }

                    Screen.Requests -> {
                        RequestsScreen(
                            requests = requests,
                            onCancelRequest = { requestId ->
                                coroutineScope.launch(Dispatchers.IO) {
                                    repository.cancelBorrowRequest(requestId)
                                }
                            },
                            onVerifyClick = { reqId, title ->
                                activeHandleOffRequestId = reqId
                                activeHandleOffItemTitle = title
                                currentScreen = Screen.HandOffVerification(reqId, title)
                            }
                        )
                    }

                    is Screen.HandOffVerification -> {
                        HandoffScreen(
                            requestId = screen.requestId,
                            itemTitle = screen.itemTitle,
                            onBackClick = { currentScreen = Screen.Requests },
                            onVerificationSuccess = {
                                coroutineScope.launch(Dispatchers.IO) {
                                    repository.updateRequestStatus(screen.requestId, "COMPLETED")
                                }
                                currentScreen = Screen.Requests
                            }
                        )
                    }

                    Screen.Profile -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("User Profile & Trust Score", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }

        }
    }
}