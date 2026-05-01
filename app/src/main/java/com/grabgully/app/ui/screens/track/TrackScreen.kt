package com.grabgully.app.ui.screens.track

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.grabgully.app.data.model.WatchlistItem
import com.grabgully.app.ui.components.GullyBottomNav
import com.grabgully.app.ui.components.GullyTab
import com.grabgully.app.ui.components.PlatformBadge
import com.grabgully.app.ui.screens.onboarding.OnboardingScreen
import com.grabgully.app.ui.theme.*

/**
 * Track Screen — price drop alerts and watchlist (v4.0 Teal Light Theme).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackScreen(
    currentRoute: String           = "track",
    onTabSelect:  (GullyTab) -> Unit = {},
    onDealClick:  (String) -> Unit = {},
    viewModel:    TrackViewModel   = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf("all") }
    var editingItem by remember { mutableStateOf<WatchlistItem?>(null) }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            Surface(
                color          = GlassBackground,
                tonalElevation = 0.dp,
            ) {
                Column {
                    Row(
                        modifier          = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .height(56.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "Price Tracker",
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = PlusJakartaSansFamily,
                            color      = TealPrimary,
                        )
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = viewModel::syncFromRemote) {
                            Icon(Icons.Default.Refresh, "Refresh", tint = TealPrimary)
                        }
                    }
                    HorizontalDivider(color = DividerColor)
                }
            }
        },
        bottomBar = {
            GullyBottomNav(currentRoute = currentRoute, onTabSelect = onTabSelect)
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            // Status filters
            TrackFilters(
                selected = selectedFilter,
                onSelect = { selectedFilter = it },
            )

            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TealPrimary)
                    }
                }
                uiState.error != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(uiState.error!!, color = AlertRed)
                    }
                }
                uiState.items.isEmpty() -> {
                    EmptyWatchlistState()
                }
                else -> {
                    val filtered = when (selectedFilter) {
                        "price_dropped" -> uiState.items.filter { it.isPriceDropped }
                        "alert_set"     -> uiState.items.filter { it.targetPrice != null }
                        else            -> uiState.items
                    }

                    if (filtered.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No items found for this filter.", color = TextSecondary)
                        }
                    } else {
                        LazyColumn(
                            contentPadding    = PaddingValues(start = 16.dp, end = 16.dp, bottom = 120.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            items(filtered, key = { it.id }) { item ->
                                WatchlistCard(
                                    item        = item,
                                    onClick     = { onDealClick(item.listingId) },
                                    onEditAlert = { editingItem = item },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Alert Bottom Sheet logic here... (same as before but light theme)
    if (editingItem != null) {
        var priceInput by remember(editingItem) { 
            mutableStateOf(editingItem?.targetPrice?.toInt()?.toString() ?: "") 
        }
        AlertDialog(
            onDismissRequest = { editingItem = null },
            title = { Text("Set Price Alert", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Current Price: ${editingItem?.formattedCurrentPrice}", color = TextSecondary)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = priceInput,
                        onValueChange = { priceInput = it },
                        label = { Text("Target Price (₹)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = DividerColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { 
                        priceInput.toDoubleOrNull()?.let { 
                            viewModel.setAlert(editingItem!!.listingId, it) 
                            editingItem = null
                        } 
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Save Alert")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingItem = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceLight,
        )
    }
}


// ── Filters ───────────────────────────────────────────────────────────────────

@Composable
private fun TrackFilters(selected: String, onSelect: (String) -> Unit) {
    val filters = listOf(
        "all" to "All Saved",
        "alert_set" to "Alert Set",
        "price_dropped" to "Price Dropped",
    )
    LazyRow(
        contentPadding      = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier            = Modifier.padding(vertical = 12.dp),
    ) {
        items(filters) { (id, label) ->
            val isSelected = selected == id
            FilterChip(
                selected = isSelected,
                onClick  = { onSelect(id) },
                label    = {
                    Text(
                        label,
                        color      = if (isSelected) Color.White else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    )
                },
                colors   = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = TealPrimary,
                    containerColor         = SurfaceLight,
                ),
                border   = FilterChipDefaults.filterChipBorder(
                    enabled             = true,
                    selected            = isSelected,
                    selectedBorderColor = TealPrimary,
                    borderColor         = DividerColor,
                    borderWidth         = 1.dp
                ),
                shape    = CircleShape,
            )
        }
    }
}


// ── Watchlist Card (v4.0 Teal Light Theme) ────────────────────────────────────

@Composable
private fun WatchlistCard(
    item:        WatchlistItem,
    onClick:     () -> Unit,
    onEditAlert: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = SoftShadow)
            .clickable(onClick = onClick),
        shape  = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        border = BorderStroke(1.dp, if (item.isPriceDropped) MintGreen else DividerColor),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Product image
                AsyncImage(
                    model              = item.imageUrl,
                    contentDescription = null,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BackgroundLight),
                )
                Spacer(Modifier.width(16.dp))

                // Title + Platform
                Column(modifier = Modifier.weight(1f)) {
                    PlatformBadge(platform = item.platform)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text       = item.title,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary,
                        maxLines   = 2,
                        overflow   = TextOverflow.Ellipsis,
                    )
                }

                // Mini sparkline chart (visual only)
                Box(modifier = Modifier.width(60.dp).height(40.dp)) {
                    MiniSparkline(isDropped = item.isPriceDropped)
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(Modifier.height(12.dp))

            // Bottom section: Prices and Status
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text("Current Price", fontSize = 10.sp, color = TextSecondary)
                    Text(
                        text  = item.formattedCurrentPrice,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (item.isPriceDropped) MintDark else TealPrimary,
                        fontFamily = PlusJakartaSansFamily,
                    )
                }

                // Status chip
                Surface(
                    shape  = RoundedCornerShape(50),
                    color  = if (item.isPriceDropped) MintLight else BackgroundLight,
                    border = BorderStroke(1.dp, if (item.isPriceDropped) MintGreen else DividerColor),
                    modifier = Modifier.clickable(onClick = onEditAlert),
                ) {
                    Row(
                        modifier          = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = if (item.isPriceDropped) Icons.Default.NotificationsActive else Icons.Default.EditNotifications,
                            contentDescription = null,
                            tint = if (item.isPriceDropped) MintDark else TextSecondary,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text       = if (item.isPriceDropped) "Price Gira!" else "Alert: ${item.formattedTargetPrice}",
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color      = if (item.isPriceDropped) MintDark else TextSecondary,
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun MiniSparkline(isDropped: Boolean) {
    val lineColor = if (isDropped) MintDark else TealPrimary
    val gradientColor = if (isDropped) MintLight else TealLight

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        val path = Path().apply {
            moveTo(0f, height * 0.5f)
            lineTo(width * 0.2f, height * 0.4f)
            lineTo(width * 0.4f, height * 0.7f)
            lineTo(width * 0.6f, height * 0.3f)
            lineTo(width * 0.8f, height * 0.5f)
            lineTo(width, if (isDropped) height * 0.9f else height * 0.2f)
        }

        // Draw line
        drawPath(
            path  = path,
            color = lineColor,
            style = Stroke(
                width = 2.dp.toPx(),
                cap   = StrokeCap.Round,
                join  = StrokeJoin.Round,
            )
        )

        // Draw gradient under line
        val fillPath = Path().apply {
            addPath(path)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path  = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(gradientColor.copy(alpha = 0.5f), Color.Transparent),
                startY = 0f,
                endY   = height
            )
        )
        
        // End point dot
        drawCircle(
            color  = lineColor,
            radius = 3.dp.toPx(),
            center = Offset(width, if (isDropped) height * 0.9f else height * 0.2f),
        )
    }
}


@Composable
private fun EmptyWatchlistState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Surface(
            shape = CircleShape,
            color = SurfaceLight,
            modifier = Modifier.size(100.dp).shadow(12.dp, CircleShape, spotColor = SoftShadow),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.NotificationsOff, null, tint = TextMuted, modifier = Modifier.size(40.dp))
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "Koi Alerts Nahi",
            fontSize   = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = PlusJakartaSansFamily,
            color      = TextPrimary,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Apni pasand ke deals par price drop alerts lagao aur paise bachao.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

// ── View Models & Data Models ─────────────────────────────────────────────────

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFFF4F6F8)
@androidx.compose.runtime.Composable
private fun TrackScreenPreview() {
    GrabGullyTheme { TrackScreen() }
}
