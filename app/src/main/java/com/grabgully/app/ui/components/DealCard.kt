package com.grabgully.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.grabgully.app.data.model.Deal
import com.grabgully.app.ui.theme.*

/**
 * DealCard — the core reusable deal card (v4.0 Teal/Light Theme).
 *
 * Soft shadows, 24dp rounded corners, light background, teal shimmer.
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
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue   = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 200),
        label         = "card_press_scale",
    )

    // Shimmer animation for price text (Teal & Mint)
    val shimmerTransition = rememberInfiniteTransition(label = "price_shimmer")
    val shimmerOffset by shimmerTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 1000f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_offset",
    )
    val shimmerBrush = Brush.linearGradient(
        colors = listOf(TealPrimary, MintGreen, TealPrimary),
        start  = Offset(shimmerOffset - 500f, 0f),
        end    = Offset(shimmerOffset, 0f),
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = 12.dp,
                shape     = RoundedCornerShape(24.dp),
                spotColor = SoftShadow,
                ambientColor = SoftShadow,
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onCardClick() },
                )
            },
        shape    = RoundedCornerShape(24.dp),
        colors   = CardDefaults.cardColors(containerColor = SurfaceLight),
        border   = BorderStroke(1.dp, DividerColor),
    ) {
        Column {
            // ── Product Image + Overlay Badges ─────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            ) {
                // Product image
                AsyncImage(
                    model             = deal.imageUrl,
                    contentDescription = deal.title,
                    contentScale      = ContentScale.Crop,
                    modifier          = Modifier.fillMaxSize(),
                )

                // Platform badge — top left
                PlatformBadge(
                    platform = deal.platform,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                )

                // Wishlist heart — glassmorphism circular button
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.8f))
                        .clickable {
                            wishlisted = !wishlisted
                            onWishlistClick()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (wishlisted)
                            Icons.Filled.Favorite
                        else
                            Icons.Outlined.FavoriteBorder,
                        contentDescription = if (wishlisted) "Remove from watchlist" else "Add to watchlist",
                        tint     = if (wishlisted) AlertRed else TextSecondary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            // ── Text content ───────────────────────────────────────────────
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                // Brand — uppercase, tiny, muted
                if (deal.brand.isNotBlank()) {
                    Text(
                        text          = deal.brand.uppercase(),
                        fontSize      = 10.sp,
                        fontWeight    = FontWeight.Medium,
                        fontFamily    = InterFamily,
                        color         = TextSecondary,
                        letterSpacing = 0.5.sp,
                        maxLines      = 1,
                        overflow      = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Title — 14sp semibold, 2 lines, min height for grid alignment
                Text(
                    text     = deal.title,
                    style    = MaterialTheme.typography.titleSmall.copy(
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 18.sp,
                    ),
                    color    = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.heightIn(min = 40.dp),
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Original price + discount badge row
                if (deal.hasDiscount) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier              = Modifier.padding(bottom = 4.dp),
                    ) {
                        Text(
                            text  = deal.formattedOriginalPrice,
                            style = OriginalPriceStyle,
                        )
                        SavingsBadge(percent = deal.discountPct)
                    }
                }

                // Deal price — shimmer teal gradient
                Text(
                    text  = deal.formattedDealPrice,
                    style = PriceTextStyle.copy(
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        brush      = shimmerBrush,
                    ),
                )
            }
        }
    }
}


// ── Shimmer placeholder (shown while page loads) ──────────────────────────────

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
        colors     = listOf(InactiveChipBg, Color.White, InactiveChipBg),
        start      = Offset(shimmerX - 300, 0f),
        end        = Offset(shimmerX, 0f),
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(24.dp),
        colors   = CardDefaults.cardColors(containerColor = SurfaceLight),
        border   = BorderStroke(1.dp, DividerColor),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(shimmerBrush)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Box(Modifier.fillMaxWidth(0.4f).height(10.dp).background(shimmerBrush, RoundedCornerShape(4.dp)))
                Spacer(Modifier.height(6.dp))
                Box(Modifier.fillMaxWidth().height(14.dp).background(shimmerBrush, RoundedCornerShape(4.dp)))
                Spacer(Modifier.height(4.dp))
                Box(Modifier.fillMaxWidth(0.7f).height(14.dp).background(shimmerBrush, RoundedCornerShape(4.dp)))
                Spacer(Modifier.height(10.dp))
                Box(Modifier.fillMaxWidth(0.3f).height(10.dp).background(shimmerBrush, RoundedCornerShape(4.dp)))
                Spacer(Modifier.height(6.dp))
                Box(Modifier.fillMaxWidth(0.5f).height(20.dp).background(shimmerBrush, RoundedCornerShape(4.dp)))
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF4F6F8)
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
            modifier = Modifier.width(180.dp).padding(16.dp),
        )
    }
}
