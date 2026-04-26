package com.grabgully.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.grabgully.app.data.model.Deal
import com.grabgully.app.ui.theme.*

/**
 * DealCard — the core reusable deal card.
 *
 * Layout:
 *  ┌──────────────────────────┐
 *  │  [Product Image]         │
 *  │  [PLATFORM]  [♥]         │ ← overlaid on image
 *  ├──────────────────────────┤
 *  │  Brand name (muted)      │
 *  │  Product title (2-line)  │
 *  │  ₹4,999  ₹2,499  50%OFF │
 *  └──────────────────────────┘
 *
 * @param deal           The deal to display
 * @param isWishlisted   Whether the user has this in their watchlist
 * @param onCardClick    Tap handler — navigates to CompareScreen
 * @param onWishlistClick Heart icon tap — add/remove from watchlist
 */
@Composable
fun DealCard(
    deal:             Deal,
    modifier:         Modifier     = Modifier,
    isWishlisted:     Boolean      = false,
    onCardClick:      () -> Unit   = {},
    onWishlistClick:  () -> Unit   = {},
) {
    var wishlisted by remember(isWishlisted) { mutableStateOf(isWishlisted) }

    // Shimmer animation for loading state
    val shimmerTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by shimmerTransition.animateFloat(
        initialValue  = -1000f,
        targetValue   = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_offset",
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDeep),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column {
            // ── Product Image + Overlay Badges ─────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                // Product image
                AsyncImage(
                    model             = deal.imageUrl,
                    contentDescription = deal.title,
                    contentScale      = ContentScale.Crop,
                    modifier          = Modifier.fillMaxSize(),
                )

                // Subtle gradient overlay at the bottom for badge readability
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, SurfaceDeep.copy(alpha = 0.8f))
                            )
                        )
                )

                // Platform badge — top left
                PlatformBadge(
                    platform = deal.platform,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                )

                // Wishlist heart — top right
                IconButton(
                    onClick  = {
                        wishlisted = !wishlisted
                        onWishlistClick()
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(40.dp),
                ) {
                    Icon(
                        imageVector = if (wishlisted)
                            Icons.Filled.Favorite
                        else
                            Icons.Outlined.FavoriteBorder,
                        contentDescription = if (wishlisted) "Remove from watchlist" else "Add to watchlist",
                        tint = if (wishlisted) GoldPrimary else TextPrimary,
                    )
                }
            }

            // ── Text content ───────────────────────────────────────────────
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            ) {
                // Brand
                if (deal.brand.isNotBlank()) {
                    Text(
                        text     = deal.brand,
                        style    = MaterialTheme.typography.bodySmall,
                        color    = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // Title
                Text(
                    text     = deal.title,
                    style    = MaterialTheme.typography.titleSmall,
                    color    = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    // Deal price — gold + bold
                    Text(
                        text  = deal.formattedDealPrice,
                        style = PriceTextStyle,
                    )

                    // Original price strikethrough (only if there's a discount)
                    if (deal.hasDiscount) {
                        Text(
                            text  = deal.formattedOriginalPrice,
                            style = OriginalPriceStyle,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Savings badge
                if (deal.discountPct > 0) {
                    SavingsBadge(percent = deal.discountPct)
                }
            }
        }
    }
}


// ── Shimmer placeholder (shown while image loads) ─────────────────────────────

@Composable
fun DealCardSkeleton(modifier: Modifier = Modifier) {
    val shimmer = rememberInfiniteTransition(label = "skeleton_shimmer")
    val shimmerX by shimmer.animateFloat(
        initialValue  = 0f,
        targetValue   = 1000f,
        animationSpec = infiniteRepeatable(
            tween(1000, easing = LinearEasing),
            RepeatMode.Restart,
        ),
        label = "skeleton_x",
    )
    val shimmerBrush = Brush.linearGradient(
        colors     = listOf(SurfaceDeep, SurfaceHighlight, SurfaceDeep),
        start      = Offset(shimmerX - 300, 0f),
        end        = Offset(shimmerX, 0f),
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = SurfaceDeep),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(shimmerBrush)
            )
            Column(modifier = Modifier.padding(10.dp)) {
                Box(Modifier.fillMaxWidth(0.5f).height(12.dp).background(shimmerBrush, RoundedCornerShape(4.dp)))
                Spacer(Modifier.height(6.dp))
                Box(Modifier.fillMaxWidth().height(14.dp).background(shimmerBrush, RoundedCornerShape(4.dp)))
                Spacer(Modifier.height(4.dp))
                Box(Modifier.fillMaxWidth(0.8f).height(14.dp).background(shimmerBrush, RoundedCornerShape(4.dp)))
                Spacer(Modifier.height(10.dp))
                Box(Modifier.fillMaxWidth(0.4f).height(20.dp).background(shimmerBrush, RoundedCornerShape(4.dp)))
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF08080F)
@Composable
private fun DealCardPreview() {
    com.grabgully.app.ui.theme.GrabGullyTheme {
        DealCard(
            deal = Deal(
                id            = "1",
                title         = "boAt Rockerz 450 Bluetooth On-Ear Headphones with Mic",
                brand         = "boAt",
                imageUrl      = "",
                platform      = "amazon",
                currentPrice  = 1299.0,
                originalPrice = 2990.0,
                discountPct   = 57,
                affiliateUrl  = "https://amazon.in/dp/B08X7VXDS8",
                category      = "electronics",
            ),
            modifier = Modifier.width(180.dp),
        )
    }
}
