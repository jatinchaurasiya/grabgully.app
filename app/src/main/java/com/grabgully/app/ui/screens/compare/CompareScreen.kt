package com.grabgully.app.ui.screens.compare

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.grabgully.app.data.model.PlatformPrice
import com.grabgully.app.ui.components.PlatformBadge
import com.grabgully.app.ui.components.PriceHistoryChart
import com.grabgully.app.ui.components.SavingsBadge
import com.grabgully.app.ui.components.platformStyle
import com.grabgully.app.ui.theme.*

/**
 * Compare screen — cross-platform price comparison + 90-day price history.
 *
 * Layout:
 *  ┌────────────────────────────────┐
 *  │ ← Back        [♥] [share]     │
 *  │ [Product Image 220dp]         │
 *  │ 24h price drop banner         │
 *  │ ── Price History Chart ──      │
 *  │ (Vico 90-day line chart)      │
 *  │ ── Platform Comparison ──     │
 *  │  [SABSE SASTA] Amazon ₹1,299  │ ← BUY button
 *  │  Flipkart ₹1,499              │
 *  │  Myntra ₹1,599                │
 *  └────────────────────────────────┘
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(
    listingId:   String,
    onBackClick: () -> Unit       = {},
    viewModel:   CompareViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(listingId) {
        viewModel.load(listingId)
    }

    Scaffold(
        containerColor = ObsidianBlack,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text     = uiState.result?.productTitle ?: "Compare",
                        style    = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color    = TextPrimary,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    // Watchlist heart
                    IconButton(onClick = { viewModel.toggleWatchlist(listingId) }) {
                        Icon(
                            if (uiState.isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            "Watchlist",
                            tint = if (uiState.isWishlisted) GoldPrimary else TextPrimary,
                        )
                    }
                    // Share — uses cheapest listing
                    uiState.result?.cheapest?.let { cheapest ->
                        IconButton(onClick = { viewModel.shareDeal(cheapest) }) {
                            Icon(Icons.Default.IosShare, "Share", tint = TextPrimary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDeep),
            )
        },
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = GoldPrimary)
                        Spacer(Modifier.height(12.dp))
                        Text("Price compare kar rahe hain...", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }
                }
            }

            uiState.error != null -> {
                Column(
                    Modifier.fillMaxSize().padding(padding).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(Icons.Default.Warning, null, tint = AlertRed, modifier = Modifier.size(60.dp))
                    Spacer(Modifier.height(16.dp))
                    Text(uiState.error!!, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { viewModel.load(listingId) },
                        colors  = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                        border  = BorderStroke(1.dp, GoldPrimary),
                        shape   = RoundedCornerShape(12.dp),
                    ) {
                        Text("Dobara Try Karo")
                    }
                }
            }

            uiState.result != null -> {
                val result = uiState.result!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState()),
                ) {
                    // Product image
                    AsyncImage(
                        model              = result.imageUrl,
                        contentDescription = result.productTitle,
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)),
                    )

                    // ── 24h price drop banner ──────────────────────────────
                    if (result.hasPriceDrop) {
                        Spacer(Modifier.height(12.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            color    = SavingsBg,
                            shape    = RoundedCornerShape(12.dp),
                            border   = BorderStroke(0.5.dp, SavingsGreen.copy(alpha = 0.5f)),
                        ) {
                            Row(
                                modifier          = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(Icons.Default.TrendingDown, null, tint = SavingsGreen)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Kal se ${result.formattedPriceDrop} gira! Abhi lo!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SavingsGreen,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    }

                    // ── Price history chart ────────────────────────────────
                    if (uiState.history.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        SectionHeader("90-Day Price History")
                        PriceHistoryChart(
                            history  = uiState.history,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }

                    // ── Platform comparison ────────────────────────────────
                    Spacer(Modifier.height(20.dp))
                    SectionHeader("Platform Compare", sub = "${result.listings.size} platforms checked")

                    result.listings
                        .sortedBy { it.currentPrice }
                        .forEach { listing ->
                            Spacer(Modifier.height(8.dp))
                            PlatformPriceCard(
                                listing    = listing,
                                isBest     = listing.isCheapest,
                                onBuyClick = { viewModel.openAffiliate(listing) },
                            )
                        }

                    Spacer(Modifier.height(80.dp))
                }
            }
        }
    }
}


// ── Section header ────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String, sub: String = "") {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        if (sub.isNotBlank()) {
            Text(sub, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
    }
}


// ── Platform price card ───────────────────────────────────────────────────────

@Composable
fun PlatformPriceCard(
    listing:    PlatformPrice,
    isBest:     Boolean = false,
    onBuyClick: () -> Unit = {},
    modifier:   Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .then(
                if (isBest) Modifier.border(1.5.dp, GoldPrimary, RoundedCornerShape(16.dp))
                else Modifier
            ),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isBest) GoldSurface else SurfaceDeep,
        ),
    ) {
        Column {
            Row(
                modifier          = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PlatformBadge(platform = listing.platform)

                if (!listing.inStock) {
                    Spacer(Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = AlertBg,
                    ) {
                        Text(
                            "Out of Stock",
                            style    = BadgeTextStyle,
                            color    = AlertRed,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text  = listing.formattedPrice,
                        style = PriceTextStyle,
                        color = if (isBest) GoldPrimary else TextPrimary,
                    )
                    if (listing.discountPct > 0) {
                        SavingsBadge(percent = listing.discountPct)
                    }
                }

                Spacer(Modifier.width(12.dp))

                Button(
                    onClick  = onBuyClick,
                    enabled  = listing.inStock,
                    colors   = ButtonDefaults.buttonColors(
                        containerColor         = if (isBest) GoldPrimary else SurfaceHighlight,
                        contentColor           = if (isBest) ObsidianBlack else TextPrimary,
                        disabledContainerColor = SurfaceRaised,
                        disabledContentColor   = TextMuted,
                    ),
                    shape    = RoundedCornerShape(10.dp),
                ) {
                    Text(
                        text  = if (isBest) "Buy! 🔥" else if (listing.inStock) "Go" else "OOS",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }

            // Gold "SABSE SASTA" footer
            if (isBest) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color    = GoldPrimary.copy(alpha = 0.15f),
                    shape    = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                ) {
                    Text(
                        "⭐  SABSE SASTA — Best price across all platforms",
                        style    = BadgeTextStyle,
                        color    = GoldPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }
        }
    }
}
