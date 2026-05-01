package com.grabgully.app.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.grabgully.app.data.model.Deal
import com.grabgully.app.ui.components.*
import com.grabgully.app.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Home screen — the main deal feed (v4.0 Teal Light Theme).
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onDealClick:       (Deal) -> Unit  = {},
    onSearchClick:     () -> Unit      = {},
    onUrlPasteClick:   () -> Unit      = {},
    onWishlistClick:   (Deal) -> Unit  = {},
    currentRoute:      String          = "home",
    onTabSelect:       (GullyTab) -> Unit = {},
    viewModel:         HomeViewModel   = hiltViewModel(),
) {
    val uiState   by viewModel.uiState.collectAsState()
    val dealsFeed = viewModel.dealsFeed.collectAsLazyPagingItems()

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            GullyTopBar(onSearchClick = onSearchClick)
        },
        bottomBar = {
            GullyBottomNav(
                currentRoute = currentRoute,
                onTabSelect  = onTabSelect,
            )
        },
        floatingActionButton = {
            // Extended Teal FAB — URL paste shortcut
            ExtendedFloatingActionButton(
                onClick          = onUrlPasteClick,
                containerColor   = TealPrimary,
                contentColor     = Color.White,
                shape            = RoundedCornerShape(24.dp),
            ) {
                Icon(Icons.Default.Link, contentDescription = "Paste URL", modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "Paste URL",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 14.sp,
                    fontFamily = InterFamily,
                )
            }
        },
    ) { innerPadding ->
        LazyVerticalStaggeredGrid(
            columns                  = StaggeredGridCells.Fixed(2),
            modifier                 = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalItemSpacing      = 12.dp,
            horizontalArrangement    = Arrangement.spacedBy(12.dp),
            contentPadding           = PaddingValues(
                start  = 16.dp,
                end    = 16.dp,
                bottom = 120.dp, // FAB + floating nav clearance
            ),
        ) {
            // ── Category chips ────────────────────────────────────────────
            item(span = StaggeredGridItemSpan.FullLine) {
                Spacer(Modifier.height(8.dp))
                CategoryChipRow(
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelect = viewModel::selectCategory,
                )
                Spacer(Modifier.height(16.dp))
            }

            // ── Hero banner ───────────────────────────────────────────────
            item(span = StaggeredGridItemSpan.FullLine) {
                HeroBanner(
                    deals       = uiState.topDeals,
                    isLoading   = uiState.isLoadingBanner,
                    onDealClick = onDealClick,
                )
                Spacer(Modifier.height(24.dp))
            }

            // ── Section header ────────────────────────────────────────────
            item(span = StaggeredGridItemSpan.FullLine) {
                SectionHeader(
                    subtitle = "Trending Now",
                    title    = "Aaj Ke Dhamakedar Deals",
                    ctaLabel = "Sab Dekho",
                    onCtaClick = {},
                )
                Spacer(Modifier.height(12.dp))
            }

            // ── Deal grid ─────────────────────────────────────────────────
            when {
                dealsFeed.loadState.refresh is LoadState.Loading -> {
                    items(6) {
                        DealCardSkeleton()
                    }
                }
                dealsFeed.loadState.refresh is LoadState.Error -> {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Column(
                            modifier              = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment   = Alignment.CenterHorizontally,
                        ) {
                            Text("Oops! Kuch gadbad hui.", color = TextSecondary)
                            Spacer(Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { dealsFeed.retry() },
                                colors  = ButtonDefaults.outlinedButtonColors(contentColor = TealPrimary),
                                border  = BorderStroke(1.dp, TealPrimary),
                            ) {
                                Text("Dobara Try Karo")
                            }
                        }
                    }
                }
                else -> {
                    items(
                        count = dealsFeed.itemCount,
                        key   = dealsFeed.itemKey { it.id },
                    ) { index ->
                        val deal = dealsFeed[index]
                        if (deal != null) {
                            DealCard(
                                deal           = deal,
                                onCardClick    = { onDealClick(deal) },
                                onWishlistClick = { onWishlistClick(deal) },
                            )
                        } else {
                            DealCardSkeleton()
                        }
                    }

                    // Append loading indicator
                    if (dealsFeed.loadState.append is LoadState.Loading) {
                        items(2) { DealCardSkeleton() }
                    }
                }
            }
        }
    }
}


// ── GullyTopBar (Clean Light variant) ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GullyTopBar(
    xpLevel:      Int    = 2,
    onSearchClick: () -> Unit = {},
) {
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
                // Logo
                Icon(
                    imageVector        = Icons.Default.ShoppingBag,
                    contentDescription = "Grab Gully",
                    tint               = TealPrimary,
                    modifier           = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text       = "GRAB GULLY",
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = PlusJakartaSansFamily,
                    color      = TealPrimary,
                    letterSpacing = (-0.5).sp,
                )

                Spacer(Modifier.weight(1f))

                // Search icon
                IconButton(onClick = onSearchClick) {
                    Icon(Icons.Outlined.Search, "Search", tint = TextPrimary)
                }

                // XP Chip
                XpChip(level = xpLevel)
            }
            HorizontalDivider(color = DividerColor)
        }
    }
}

@Composable
private fun XpChip(level: Int) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MintLight,
        border = BorderStroke(1.dp, MintGreen),
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector        = Icons.Default.Bolt,
                contentDescription = null,
                tint               = MintDark,
                modifier           = Modifier.size(14.dp),
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text       = "Level $level",
                fontSize   = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PlusJakartaSansFamily,
                color      = MintDark,
                letterSpacing = (-0.3).sp,
            )
        }
    }
}


// ── HeroBanner (Light theme gradients) ─────────────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeroBanner(
    deals:       List<Deal>,
    isLoading:   Boolean    = false,
    onDealClick: (Deal) -> Unit = {},
    modifier:    Modifier   = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { maxOf(deals.size, 1) })

    // Auto-scroll every 3 seconds
    LaunchedEffect(deals.size) {
        while (true) {
            delay(3000)
            if (deals.isNotEmpty()) {
                val next = (pagerState.currentPage + 1) % deals.size
                pagerState.animateScrollToPage(next)
            }
        }
    }

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(24.dp), spotColor = SoftShadow, ambientColor = SoftShadow)
        ) {
            HorizontalPager(
                state    = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(24.dp)),
            ) { page ->
                if (isLoading || deals.isEmpty()) {
                    // Skeleton
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(SurfaceLight),
                    )
                } else {
                    val deal = deals[page]
                    HeroBannerCard(deal = deal, onClick = { onDealClick(deal) })
                }
            }

            // Page indicator dots — overlaid at bottom center
            if (deals.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    repeat(deals.size) { index ->
                        val selected = index == pagerState.currentPage
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(
                                    width  = if (selected) 24.dp else 6.dp,
                                    height = 6.dp,
                                )
                                .clip(RoundedCornerShape(50))
                                .background(if (selected) TealPrimary else Color.White.copy(alpha = 0.5f)),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroBannerCard(deal: Deal, onClick: () -> Unit) {
    // Countdown timer state (visual-only placeholder)
    var timeLeft by remember { mutableIntStateOf(8095) } // ~2h 14m 55s
    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
    }
    val hours   = timeLeft / 3600
    val minutes = (timeLeft % 3600) / 60
    val seconds = timeLeft % 60

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model              = deal.imageUrl,
            contentDescription = deal.title,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier.fillMaxSize(),
        )

        // Light gradient overlay — top and bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.2f),
                            Color.Transparent,
                            Color.White.copy(alpha = 0.95f),
                        ),
                    )
                )
        )

        // "BEST DEAL TODAY" badge — top left
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            shape = RoundedCornerShape(50),
            color = MintGreen,
        ) {
            Text(
                text          = "BEST DEAL TODAY",
                fontSize      = 10.sp,
                fontWeight    = FontWeight.ExtraBold,
                fontFamily    = PlusJakartaSansFamily,
                color         = MintDark,
                letterSpacing = 1.sp,
                modifier      = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }

        // Deal info at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 32.dp, end = 20.dp),
        ) {
            Text(
                text       = deal.title,
                fontSize   = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = PlusJakartaSansFamily,
                color      = TextPrimary,
                maxLines   = 2,
                overflow   = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Price
                Text(
                    text  = deal.formattedDealPrice,
                    style = HeroPriceStyle,
                )

                // Countdown timer badge
                Surface(
                    shape  = RoundedCornerShape(8.dp),
                    color  = AlertBg,
                    border = BorderStroke(0.5.dp, AlertRed.copy(alpha = 0.5f)),
                ) {
                    Row(
                        modifier          = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Schedule,
                            contentDescription = null,
                            tint               = AlertRed,
                            modifier           = Modifier.size(14.dp),
                        )
                        Text(
                            text       = "%02d:%02d:%02d".format(hours, minutes, seconds),
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PlusJakartaSansFamily,
                            color      = AlertRed,
                        )
                    }
                }
            }
        }
    }
}


// ── Section header (v4.0) ─────────────────────────────────────────────────────

@Composable
fun SectionHeader(
    title:      String,
    subtitle:   String   = "",
    ctaLabel:   String   = "",
    onCtaClick: () -> Unit = {},
    modifier:   Modifier = Modifier,
) {
    Row(
        modifier              = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.Bottom,
    ) {
        Column {
            if (subtitle.isNotBlank()) {
                Text(
                    text          = subtitle.uppercase(),
                    style         = SectionSubtitleStyle,
                    letterSpacing = 2.sp,
                )
                Spacer(Modifier.height(2.dp))
            }
            Text(
                text       = title,
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PlusJakartaSansFamily,
                color      = TextPrimary,
            )
        }
        if (ctaLabel.isNotBlank()) {
            TextButton(onClick = onCtaClick) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        ctaLabel,
                        color      = TealPrimary,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFamily,
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint     = TealPrimary,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF4F6F8, widthDp = 390, heightDp = 800)
@Composable
private fun HomeScreenPreview() {
    GrabGullyTheme {
        HomeScreen()
    }
}
