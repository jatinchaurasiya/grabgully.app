package com.grabgully.app.ui.screens.track

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.grabgully.app.data.model.WatchlistItem
import com.grabgully.app.ui.components.GullyBottomNav
import com.grabgully.app.ui.components.GullyTab
import com.grabgully.app.ui.components.SavingsBadge
import com.grabgully.app.ui.components.PlatformBadge
import com.grabgully.app.ui.theme.*

/**
 * Track Screen — offline-first watchlist.
 *
 * Layout:
 *  ┌──────────────────────────────┐
 *  │ Nazar Mein  [sync icon]      │
 *  │ 3 items being tracked        │
 *  ├──────────────────────────────┤
 *  │ [Product image] [title]      │  ← swipe-left to delete
 *  │ ₹1,299  Alert: ₹999  [bell]  │
 *  │ Status: Price Gira! / Alert  │
 *  └──────────────────────────────┘
 *
 * Swipe-to-delete uses M3 SwipeToDismissBox.
 * Alert bottom sheet lets user set a target price.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackScreen(
    onDealClick:  (String) -> Unit   = {},
    currentRoute: String             = "track",
    onTabSelect:  (GullyTab) -> Unit = {},
    viewModel:    TrackViewModel     = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var alertItem by remember { mutableStateOf<WatchlistItem?>(null) }

    Scaffold(
        containerColor = ObsidianBlack,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Nazar Mein 👁️", style = MaterialTheme.typography.headlineMedium, color = GoldPrimary)
                        Text(
                            "${uiState.items.size} items being tracked",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                        )
                    }
                },
                actions = {
                    if (uiState.isSyncing) {
                        CircularProgressIndicator(
                            color    = GoldPrimary,
                            modifier = Modifier.size(20.dp).padding(end = 8.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        IconButton(onClick = viewModel::syncFromRemote) {
                            Icon(Icons.Default.Refresh, "Sync", tint = TextSecondary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDeep),
            )
        },
        bottomBar = {
            GullyBottomNav(currentRoute = currentRoute, onTabSelect = onTabSelect)
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = GoldPrimary)
                    }
                }

                uiState.items.isEmpty() -> {
                    EmptyTrackState()
                }

                else -> {
                    LazyColumn(
                        contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        // Summary chips at top
                        item {
                            TrackSummaryRow(items = uiState.items)
                            Spacer(Modifier.height(8.dp))
                        }

                        items(
                            items = uiState.items,
                            key   = { it.id },
                        ) { item ->
                            SwipeToDeleteWrapper(
                                onDelete = { viewModel.removeItem(item.id) },
                            ) {
                                WatchlistCard(
                                    item          = item,
                                    onCardClick   = { onDealClick(item.listingId) },
                                    onAlertClick  = { alertItem = item },
                                )
                            }
                        }
                        item { Spacer(Modifier.height(60.dp)) }
                    }
                }
            }

            // Error snackbar
            uiState.error?.let { err ->
                Snackbar(
                    modifier      = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                    action        = {
                        TextButton(onClick = viewModel::clearError) {
                            Text("OK", color = GoldPrimary)
                        }
                    },
                    containerColor = AlertBg,
                ) {
                    Text(err, color = AlertRed)
                }
            }
        }

        // Alert bottom sheet
        alertItem?.let { item ->
            SetAlertBottomSheet(
                item        = item,
                onDismiss   = { alertItem = null },
                onSetAlert  = { price ->
                    viewModel.setAlert(item.id, price)
                    alertItem = null
                },
            )
        }
    }
}


// ── Watchlist card ─────────────────────────────────────────────────────────────

@Composable
fun WatchlistCard(
    item:         WatchlistItem,
    onCardClick:  () -> Unit = {},
    onAlertClick: () -> Unit = {},
    modifier:     Modifier   = Modifier,
) {
    val statusColor = when {
        item.isPriceDropped -> SavingsGreen
        item.targetPrice != null -> InfoBlue
        else -> TextMuted
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isPriceDropped) SavingsBg else SurfaceDeep,
        ),
        border = if (item.isPriceDropped)
            BorderStroke(1.dp, SavingsGreen.copy(alpha = 0.6f))
        else null,
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Product image thumbnail
            AsyncImage(
                model              = item.imageUrl,
                contentDescription = item.title,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceRaised),
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Platform badge
                PlatformBadge(platform = item.platform)
                Spacer(Modifier.height(4.dp))

                // Title
                Text(
                    text     = item.title,
                    style    = MaterialTheme.typography.titleSmall,
                    color    = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(Modifier.height(6.dp))

                // Prices row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text  = item.formattedCurrentPrice,
                        style = PriceTextStyle,
                    )
                    if (item.targetPrice != null) {
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            imageVector        = Icons.Default.NotificationsActive,
                            contentDescription = "Alert set",
                            tint               = statusColor,
                            modifier           = Modifier.size(14.dp),
                        )
                        Text(
                            text  = " ${item.formattedTargetPrice}",
                            style = MaterialTheme.typography.bodySmall,
                            color = statusColor,
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                // Status chip
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = statusColor.copy(alpha = 0.15f),
                ) {
                    Text(
                        text  = item.statusLabel,
                        style = BadgeTextStyle,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
            }

            // Bell / alert button
            IconButton(onClick = onAlertClick) {
                Icon(
                    imageVector        = if (item.targetPrice != null)
                        Icons.Default.NotificationsActive
                    else Icons.Default.NotificationsNone,
                    contentDescription = "Set alert",
                    tint               = if (item.targetPrice != null) GoldPrimary else TextMuted,
                )
            }
        }
    }
}


// ── Swipe-to-delete wrapper ───────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteWrapper(
    onDelete: () -> Unit,
    content:  @Composable () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state            = dismissState,
        backgroundContent = {
            Box(
                modifier          = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AlertBg),
                contentAlignment  = Alignment.CenterEnd,
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint               = AlertRed,
                    modifier           = Modifier.padding(end = 24.dp),
                )
            }
        },
        enableDismissFromStartToEnd = false,
        content                     = { content() },
    )
}


// ── Summary row ───────────────────────────────────────────────────────────────

@Composable
private fun TrackSummaryRow(items: List<WatchlistItem>) {
    val dropped  = items.count { it.isPriceDropped }
    val alerted  = items.count { it.targetPrice != null }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (dropped > 0) {
            SummaryChip("$dropped Price Gira 🎉", SavingsGreen)
        }
        if (alerted > 0) {
            SummaryChip("$alerted Alert Set 🔔", InfoBlue)
        }
    }
}

@Composable
private fun SummaryChip(label: String, color: androidx.compose.ui.graphics.Color) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(0.5.dp, color.copy(alpha = 0.4f)),
    ) {
        Text(
            label,
            style    = MaterialTheme.typography.labelSmall,
            color    = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}


// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyTrackState() {
    Column(
        modifier              = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.Center,
    ) {
        Text("👁️", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(16.dp))
        Text(
            "Abhi kuch nahi track ho raha",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Kisi bhi deal par ♥ press karo\naur hum price track karenge!",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
    }
}


// ── Set alert bottom sheet ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetAlertBottomSheet(
    item:       WatchlistItem,
    onDismiss:  () -> Unit,
    onSetAlert: (Double) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var priceInput by remember { mutableStateOf(item.targetPrice?.let { "%.0f".format(it) } ?: "") }
    val isValid    = priceInput.toDoubleOrNull()?.let { it > 0 } ?: false

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        containerColor   = SurfaceRaised,
    ) {
        Column(
            modifier            = Modifier.padding(24.dp).navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Price Alert Set Karo 🔔", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)

            Text(
                item.title,
                style    = MaterialTheme.typography.bodyMedium,
                color    = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                "Current price: ${item.formattedCurrentPrice}",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
            )

            OutlinedTextField(
                value         = priceInput,
                onValueChange = { priceInput = it.filter { c -> c.isDigit() } },
                label         = { Text("Target price (₹)", color = TextMuted) },
                prefix        = { Text("₹", color = GoldPrimary) },
                singleLine    = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors          = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = GoldPrimary,
                    unfocusedBorderColor = DividerColor,
                    focusedTextColor     = TextPrimary,
                    unfocusedTextColor   = TextPrimary,
                    cursorColor          = GoldPrimary,
                ),
                shape   = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick  = { priceInput.toDoubleOrNull()?.let { onSetAlert(it) } },
                enabled  = isValid,
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor   = ObsidianBlack,
                ),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text("Alert Lagao", style = MaterialTheme.typography.labelLarge)
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}
