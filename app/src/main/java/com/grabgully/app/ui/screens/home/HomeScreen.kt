package com.grabgully.app.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
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
 * Home screen — the main deal feed.
 *
 * Layout:
 * 1. GullyTopBar (logo + search + XP chip)
 * 2. CategoryChipRow (horizontal filter)
 * 3. HeroBanner (top 5 deals carousel, auto-scrolls 3s)
 * 4. "Aaj Ke Dhamakedar Deals" section header
 * 5. LazyVerticalStaggeredGrid (2-col) — infinite scroll via Paging 3
 * 6. BottomNav + GoldFAB
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
        containerColor = ObsidianBlack,
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
            // Gold FAB — URL paste shortcut
            FloatingActionButton(
                onClick          = onUrlPasteClick,
                containerColor   = GoldPrimary,
                contentColor     = ObsidianBlack,
                shape            = RoundedCornerShape(16.dp),
            ) {
                Icon(Icons.Default.Link, contentDescription = "Paste product URL")
            }
        },
    ) { innerPadding ->
        LazyVerticalStaggeredGrid(
            columns                  = StaggeredGridCells.Fixed(2),
            modifier                 = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalItemSpacing      = 10.dp,
            horizontalArrangement    = Arrangement.spacedBy(10.dp),
            contentPadding           = PaddingValues(
                start  = 12.dp,
                end    = 12.dp,
                bottom = 80.dp, // FAB clearance
            ),
        ) {
            // ── Category chips ────────────────────────────────────────────
            item(span = StaggeredGridItemSpan.FullLine) {
                Spacer(Modifier.height(8.dp))
                CategoryChipRow(
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelect = viewModel::selectCategory,
                )
                Spacer(Modifier.height(12.dp))
            }

            // ── Hero banner ───────────────────────────────────────────────
            item(span = StaggeredGridItemSpan.FullLine) {
                HeroBanner(
                    deals       = uiState.topDeals,
                    isLoading   = uiState.isLoadingBanner,
                    onDealClick = onDealClick,
                )
                Spacer(Modifier.height(16.dp))
            }

            // ── Section header ────────────────────────────────────────────
            item(span = StaggeredGridItemSpan.FullLine) {
                SectionHeader(
                    title    = "Aaj Ke Dhamakedar Deals",
                    ctaLabel = "Sab Dekho",
                    onCtaClick = {},
                )
                Spacer(Modifier.height(8.dp))
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
                                colors  = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                                border  = BorderStroke(1.dp, GoldPrimary),
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


// ── GullyTopBar ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GullyTopBar(
    xpLevel:      Int    = 2,
    onSearchClick: () -> Unit = {},
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = Icons.Default.ShoppingBag,
                    contentDescription = "Grab Gully",
                    tint               = GoldPrimary,
                    modifier           = Modifier.size(26.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text      = "GRAB GULLY",
                    style     = MaterialTheme.typography.headlineMedium.copy(
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color      = GoldPrimary,
                    ),
                )
            }
        },
        actions = {
            // Search icon
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, "Search", tint = TextPrimary)
            }
            // XP Chip
            XpChip(level = xpLevel)
            Spacer(Modifier.width(8.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = SurfaceDeep,
        ),
        modifier = Modifier.drawBehind {
            drawLine(
                color       = GoldPrimary.copy(alpha = 0.5f),
                start       = Offset(0f, size.height),
                end         = Offset(size.width, size.height),
                strokeWidth = 1.dp.toPx(),
            )
        },
    )
}

@Composable
private fun XpChip(level: Int) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = GoldSurface,
        modifier = Modifier.border(0.5.dp, GoldPrimary, RoundedCornerShape(20.dp)),
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector        = Icons.Default.Bolt,
                contentDescription = null,
                tint               = XpGold,
                modifier           = Modifier.size(14.dp),
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text  = "Level $level",
                style = MaterialTheme.typography.labelSmall,
                color = GoldPrimary,
            )
        }
    }
}


// ── HeroBanner ────────────────────────────────────────────────────────────────

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
        HorizontalPager(
            state    = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp)),
        ) { page ->
            if (isLoading || deals.isEmpty()) {
                // Skeleton
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(SurfaceRaised),
                )
            } else {
                val deal = deals[page]
                HeroBannerCard(deal = deal, onClick = { onDealClick(deal) })
            }
        }

        // Page indicator dots
        if (deals.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(deals.size) { index ->
                    val selected = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(if (selected) 20.dp else 6.dp, 6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (selected) GoldPrimary else DividerColor),
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroBannerCard(deal: Deal, onClick: () -> Unit) {
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

        // Dark gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, ObsidianBlack.copy(alpha = 0.85f)),
                        startY = 80f,
                    )
                )
        )

        // "BEST DEAL TODAY" badge
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            shape = RoundedCornerShape(6.dp),
            color = GoldPrimary.copy(alpha = 0.92f),
        ) {
            Text(
                text     = "BEST DEAL TODAY",
                style    = BadgeTextStyle,
                color    = ObsidianBlack,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }

        // Deal info at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp),
        ) {
            Text(
                text     = deal.title,
                style    = MaterialTheme.typography.titleMedium,
                color    = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text  = deal.formattedDealPrice,
                    style = PriceTextStyle.copy(fontSize = 22.sp),
                )
                if (deal.hasDiscount) {
                    Spacer(Modifier.width(8.dp))
                    SavingsBadge(percent = deal.discountPct)
                }
            }
        }
    }
}


// ── Section header ────────────────────────────────────────────────────────────

@Composable
fun SectionHeader(
    title:      String,
    ctaLabel:   String   = "",
    onCtaClick: () -> Unit = {},
    modifier:   Modifier = Modifier,
) {
    Row(
        modifier              = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(
            text  = title,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
        )
        if (ctaLabel.isNotBlank()) {
            TextButton(onClick = onCtaClick) {
                Text(ctaLabel, color = GoldPrimary)
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF08080F, widthDp = 390, heightDp = 800)
@Composable
private fun HomeScreenPreview() {
    GrabGullyTheme {
        HomeScreen()
    }
}
