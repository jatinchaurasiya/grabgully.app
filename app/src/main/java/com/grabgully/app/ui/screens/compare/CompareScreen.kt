package com.grabgully.app.ui.screens.compare

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.grabgully.app.data.model.PlatformPrice
import com.grabgully.app.ui.components.PlatformBadge
import com.grabgully.app.ui.components.PriceHistoryChart
import com.grabgully.app.ui.components.SavingsBadge
import com.grabgully.app.ui.components.platformStyle
import com.grabgully.app.ui.theme.*

/**
 * Compare screen — cross-platform price comparison (v4.0 Teal Light Theme).
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
        containerColor = BackgroundLight,
        topBar = {
            // Light Top Bar
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
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = TealPrimary)
                        }
                        Text(
                            text       = "Compare Karo",
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PlusJakartaSansFamily,
                            color      = TealPrimary,
                            letterSpacing = (-0.3).sp,
                        )
                        Spacer(Modifier.weight(1f))
                        // Share button
                        uiState.result?.cheapest?.let { cheapest ->
                            IconButton(onClick = { viewModel.shareDeal(cheapest) }) {
                                Icon(Icons.Default.IosShare, "Share", tint = TealPrimary)
                            }
                        }
                    }
                    HorizontalDivider(color = DividerColor)
                }
            }
        },
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = TealPrimary)
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
                        colors  = ButtonDefaults.outlinedButtonColors(contentColor = TealPrimary),
                        border  = BorderStroke(1.dp, TealPrimary),
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
                    // ── Product hero image ───────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                    ) {
                        AsyncImage(
                            model              = result.imageUrl,
                            contentDescription = result.productTitle,
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier.fillMaxSize(),
                        )
                        // Gradient overlay (Light to White)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.White.copy(alpha = 0.5f),
                                            BackgroundLight,
                                        ),
                                    )
                                )
                        )

                        // Price Verified badge — top right
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp),
                            shape = RoundedCornerShape(50),
                            color = SurfaceLight,
                            shadowElevation = 8.dp,
                        ) {
                            Row(
                                modifier          = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    null,
                                    tint     = MintDark,
                                    modifier = Modifier.size(16.dp),
                                )
                                Text(
                                    "PRICE VERIFIED",
                                    fontSize      = 11.sp,
                                    fontWeight    = FontWeight.Bold,
                                    fontFamily    = InterFamily,
                                    color         = MintDark,
                                    letterSpacing = 1.sp,
                                )
                            }
                        }
                    }

                    // ── Product info card ─────────────────────────────────────
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .offset(y = (-40).dp)
                            .shadow(
                                elevation = 16.dp,
                                shape     = RoundedCornerShape(24.dp),
                                spotColor = SoftShadow,
                                ambientColor = SoftShadow,
                            ),
                        shape = RoundedCornerShape(24.dp),
                        color = SurfaceLight,
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            val brand = result.productTitle.split(" ").firstOrNull()?.uppercase() ?: ""
                            Text(
                                text          = brand,
                                fontSize      = 12.sp,
                                fontWeight    = FontWeight.Bold,
                                fontFamily    = InterFamily,
                                color         = TealPrimary,
                                letterSpacing = 2.sp,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text       = result.productTitle,
                                fontSize   = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = PlusJakartaSansFamily,
                                color      = TextPrimary,
                                lineHeight = 28.sp,
                            )
                            Spacer(Modifier.height(12.dp))
                            // Rating row
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MintLight,
                                ) {
                                    Row(
                                        modifier          = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(
                                            Icons.Default.Star,
                                            null,
                                            tint     = MintDark,
                                            modifier = Modifier.size(14.dp),
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            "4.2",
                                            fontSize   = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color      = MintDark,
                                        )
                                    }
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "255 Ratings",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color    = TextSecondary,
                                )
                            }
                        }
                    }

                    // ── 24h price drop banner ────────────────────────────────
                    if (result.hasPriceDrop) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .offset(y = (-20).dp),
                            color  = MintLight,
                            shape  = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MintGreen),
                        ) {
                            Row(
                                modifier          = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                // Green circle with down arrow
                                Surface(
                                    shape = CircleShape,
                                    color = MintGreen,
                                    modifier = Modifier.size(32.dp),
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Icon(
                                            Icons.Default.ArrowDownward,
                                            null,
                                            tint     = MintDark,
                                            modifier = Modifier.size(18.dp),
                                        )
                                    }
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "Kal se ${result.formattedPriceDrop} sasta hua!",
                                    fontSize   = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = MintDark,
                                )
                                Spacer(Modifier.weight(1f))
                                Icon(Icons.Default.TrendingDown, null, tint = MintDark.copy(alpha = 0.6f))
                            }
                        }
                    }

                    // ── Platform comparison ──────────────────────────────────
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text          = "BEST DEALS",
                        modifier      = Modifier.padding(horizontal = 20.dp),
                        fontSize      = 12.sp,
                        fontWeight    = FontWeight.Bold,
                        fontFamily    = PlusJakartaSansFamily,
                        color         = TextSecondary,
                        letterSpacing = 2.sp,
                    )
                    Spacer(Modifier.height(12.dp))

                    result.listings
                        .sortedBy { it.currentPrice }
                        .forEach { listing ->
                            PlatformPriceCard(
                                listing    = listing,
                                isBest     = listing.isCheapest,
                                onBuyClick = { viewModel.openAffiliate(listing) },
                            )
                            Spacer(Modifier.height(12.dp))
                        }

                    // ── Price history chart ──────────────────────────────────
                    if (uiState.history.isNotEmpty()) {
                        Spacer(Modifier.height(24.dp))
                        Text(
                            text          = "90 DIN KA PRICE JOURNEY",
                            modifier      = Modifier.padding(horizontal = 20.dp),
                            fontSize      = 12.sp,
                            fontWeight    = FontWeight.Bold,
                            fontFamily    = PlusJakartaSansFamily,
                            color         = TextSecondary,
                            letterSpacing = 2.sp,
                        )
                        Spacer(Modifier.height(12.dp))
                        Surface(
                            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                            shape    = RoundedCornerShape(24.dp),
                            color    = SurfaceLight,
                            border   = BorderStroke(1.dp, DividerColor),
                        ) {
                            PriceHistoryChart(
                                history  = uiState.history,
                                modifier = Modifier.padding(16.dp),
                            )
                        }
                    }

                    // ── Trust row ────────────────────────────────────────────
                    Spacer(Modifier.height(24.dp))
                    Row(
                        modifier              = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        TrustIndicator(Icons.Default.Verified, "Verified Price")
                        TrustIndicator(Icons.Default.Schedule, "Updated: 5 min ago")
                        TrustIndicator(Icons.Default.Link, "Affiliate Link")
                    }

                    // ── Main CTA ─────────────────────────────────────────────
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick  = { viewModel.toggleWatchlist(listingId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(50), spotColor = TealPrimary.copy(alpha = 0.5f)),
                        shape    = RoundedCornerShape(50),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = TealPrimary,
                            contentColor   = Color.White,
                        ),
                        contentPadding = PaddingValues(vertical = 18.dp),
                    ) {
                        Icon(Icons.Default.NotificationsActive, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Price Alert Lagao",
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PlusJakartaSansFamily,
                        )
                    }

                    Spacer(Modifier.height(80.dp))
                }
            }
        }
    }
}


// ── Trust indicator ───────────────────────────────────────────────────────────

@Composable
private fun TrustIndicator(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier              = Modifier.alpha(0.7f),
    ) {
        Icon(icon, null, modifier = Modifier.size(16.dp), tint = TextSecondary)
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
    }
}

private fun Modifier.alpha(alpha: Float): Modifier = this.then(
    Modifier.graphicsLayer(alpha = alpha)
)


// ── Platform price card (v4.0 Teal Light Theme) ───────────────────────────────

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
                if (isBest) Modifier.shadow(16.dp, RoundedCornerShape(24.dp), spotColor = SoftShadow, ambientColor = SoftShadow)
                else Modifier
            ),
        shape  = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = if (isBest) TealPrimary else SurfaceLight),
        border = if (isBest) null else BorderStroke(1.dp, DividerColor),
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Left: platform + price
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    // Platform pill
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (isBest) TealDark.copy(alpha = 0.5f) else InactiveChipBg,
                    ) {
                        Text(
                            listing.platform.replaceFirstChar { it.uppercase() },
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color      = if (isBest) Color.White else TextPrimary,
                            modifier   = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                    // "SABSE SASTA" badge
                    if (isBest) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MintGreen,
                        ) {
                            Text(
                                "SABSE SASTA",
                                fontSize      = 9.sp,
                                fontWeight    = FontWeight.ExtraBold,
                                color         = MintDark,
                                letterSpacing = (-0.3).sp,
                                modifier      = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text  = listing.formattedPrice,
                    fontSize = if (isBest) 26.sp else 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = PlusJakartaSansFamily,
                    color = if (isBest) Color.White else TextPrimary,
                )
            }

            // Right: buy button
            OutlinedButton(
                onClick = onBuyClick,
                enabled = listing.inStock,
                shape   = RoundedCornerShape(50),
                border  = BorderStroke(
                    1.dp,
                    if (isBest) Color.White.copy(alpha = 0.3f) else DividerColor,
                ),
                colors  = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isBest) Color.White else TealPrimary,
                    containerColor = if (isBest) TealDark.copy(alpha = 0.3f) else Color.Transparent
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text(
                    "Yahan Se Khareedo",
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFamily,
                )
            }
        }
    }
}
