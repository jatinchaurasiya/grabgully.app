package com.grabgully.app.ui.screens.search

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.grabgully.app.data.model.Deal
import com.grabgully.app.data.model.SearchResult
import com.grabgully.app.ui.components.*
import com.grabgully.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(
    urlMode:     Boolean = false,
    onDealClick: (Deal) -> Unit = {},
    onBackClick: () -> Unit = {},
    currentRoute: String = "search",
    onTabSelect:  (GullyTab) -> Unit = {},
    viewModel:   SearchViewModel = hiltViewModel(),
) {
    val uiState      by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val keyboard       = LocalSoftwareKeyboardController.current

    LaunchedEffect(urlMode) {
        if (urlMode) viewModel.setUrlMode(true)
        delay(200)
        focusRequester.requestFocus()
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            SearchTopBar(
                query          = uiState.query,
                urlMode        = uiState.urlMode,
                focusRequester = focusRequester,
                onQueryChange  = viewModel::onQueryChange,
                onSearch       = { viewModel.search(); keyboard?.hide() },
                onBackClick    = onBackClick,
                onClearClick   = viewModel::clear,
                onUrlToggle    = viewModel::toggleUrlMode,
            )
        },
        bottomBar = {
            GullyBottomNav(currentRoute = currentRoute, onTabSelect = onTabSelect)
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {

            // Platform filter chips
            Column {
                PlatformFilterRow(
                    selected        = uiState.selectedPlatform,
                    onPlatformSelect = viewModel::selectPlatform,
                )

                when {
                    uiState.isLoading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = TealPrimary)
                        }
                    }
                    uiState.results.isEmpty() && uiState.query.isNotBlank() && !uiState.isLoading -> {
                        Column(
                            Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(Icons.Default.SearchOff, null, tint = TextMuted, modifier = Modifier.size(64.dp))
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "'${uiState.query}' nahi mila",
                                color      = TextPrimary,
                                fontSize   = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = PlusJakartaSansFamily,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Doosra keyword try karo",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                    uiState.results.isEmpty() -> {
                        // Initial hint state — full discovery UI
                        SearchDiscoveryScreen()
                    }
                    else -> {
                        LazyColumn(
                            contentPadding    = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            item {
                                Text(
                                    "${uiState.results.size} results for '${uiState.query}'",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted,
                                    modifier = Modifier.padding(vertical = 4.dp),
                                )
                            }
                            items(uiState.results) { result ->
                                DealCard(
                                    deal        = result.toDeal(),
                                    onCardClick = { onDealClick(result.toDeal()) },
                                    modifier    = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// ── Search Top Bar (v4.0 Teal Light Theme) ────────────────────────────────────

@Composable
private fun SearchTopBar(
    query:          String,
    urlMode:        Boolean,
    focusRequester: FocusRequester,
    onQueryChange:  (String) -> Unit,
    onSearch:       () -> Unit,
    onBackClick:    () -> Unit,
    onClearClick:   () -> Unit,
    onUrlToggle:    () -> Unit,
) {
    Surface(color = SurfaceLight, tonalElevation = 0.dp) {
        Column {
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = TealPrimary)
                }
                OutlinedTextField(
                    value         = query,
                    onValueChange = onQueryChange,
                    modifier      = Modifier.weight(1f).focusRequester(focusRequester),
                    placeholder   = {
                        Text(
                            if (urlMode) "Product URL paste karo..." else "Kya dhundh rahe ho?",
                            color = TextMuted,
                        )
                    },
                    singleLine    = true,
                    leadingIcon   = {
                        Icon(
                            if (urlMode) Icons.Default.Link else Icons.Default.Search,
                            null,
                            tint = if (urlMode) TealPrimary else TextMuted,
                        )
                    },
                    trailingIcon  = if (query.isNotBlank()) {{
                        IconButton(onClick = onClearClick) {
                            Icon(Icons.Default.Close, "Clear", tint = TextMuted)
                        }
                    }} else null,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = TealPrimary,
                        unfocusedBorderColor = DividerColor,
                        focusedTextColor     = TextPrimary,
                        unfocusedTextColor   = TextPrimary,
                        cursorColor          = TealPrimary,
                    ),
                    shape = RoundedCornerShape(50),
                )
                // URL mode toggle
                IconButton(onClick = onUrlToggle) {
                    Icon(
                        if (urlMode) Icons.Default.TextFields else Icons.Default.Link,
                        contentDescription = "Toggle URL mode",
                        tint = if (urlMode) TealPrimary else TextMuted,
                    )
                }
            }
            HorizontalDivider(color = DividerColor)
        }
    }
}


// ── Platform filter row ───────────────────────────────────────────────────────

@Composable
private fun PlatformFilterRow(
    selected:        String?,
    onPlatformSelect: (String?) -> Unit,
) {
    val platforms = listOf("All", "Amazon", "Flipkart", "Myntra", "Meesho", "Ajio")
    LazyRow(
        contentPadding      = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier            = Modifier.padding(vertical = 12.dp),
    ) {
        items(platforms) { p ->
            val isSelected = (p == "All" && selected == null) || p.lowercase() == selected
            FilterChip(
                selected = isSelected,
                onClick  = { onPlatformSelect(if (p == "All") null else p.lowercase()) },
                label    = {
                    Text(
                        p,
                        color      = if (isSelected) Color.White else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize   = 14.sp,
                    )
                },
                colors   = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = TealPrimary,
                    containerColor         = InactiveChipBg,
                ),
                border   = FilterChipDefaults.filterChipBorder(
                    enabled             = true,
                    selected            = isSelected,
                    selectedBorderColor = TealPrimary,
                    borderColor         = InactiveChipBg,
                    borderWidth         = 0.dp
                ),
                shape    = CircleShape,
            )
        }
    }
}


// ── Discovery screen (initial state) ──────────────────────────────────────────

@Composable
private fun SearchDiscoveryScreen() {
    LazyColumn(
        contentPadding      = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        // Trending Hot Hunts
        item {
            Text(
                text          = "TRENDING HOT HUNTS",
                fontSize      = 12.sp,
                fontWeight    = FontWeight.Bold,
                fontFamily    = PlusJakartaSansFamily,
                color         = TextSecondary,
                letterSpacing = 2.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text       = "Top Searches Abhi",
                fontSize   = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = PlusJakartaSansFamily,
                color      = TextPrimary,
            )
            Spacer(Modifier.height(16.dp))
            TrendingHuntsGrid()
        }

        // Recent Searches
        item {
            Text(
                text          = "RECENT SEARCHES",
                fontSize      = 12.sp,
                fontWeight    = FontWeight.Bold,
                fontFamily    = PlusJakartaSansFamily,
                color         = TextSecondary,
                letterSpacing = 2.sp,
            )
            Spacer(Modifier.height(12.dp))
            RecentSearchesList()
        }

        // Popular Brands
        item {
            Text(
                text          = "POPULAR BRANDS",
                fontSize      = 12.sp,
                fontWeight    = FontWeight.Bold,
                fontFamily    = PlusJakartaSansFamily,
                color         = TextSecondary,
                letterSpacing = 2.sp,
            )
            Spacer(Modifier.height(16.dp))
            PopularBrandsGrid()
        }

        // Hint
        item {
            Box(
                modifier         = Modifier.fillMaxWidth().padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "Ya Amazon/Flipkart URL paste karo\nham price compare kar denge!",
                    style     = MaterialTheme.typography.bodyMedium,
                    color     = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
    }
}


// ── Trending hunts grid ───────────────────────────────────────────────────────

@Composable
private fun TrendingHuntsGrid() {
    val hunts = listOf(
        "Electronics" to "📱",
        "Fashion" to "👗",
        "Home & Kitchen" to "🏠",
        "Beauty" to "💄",
    )

    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        hunts.forEach { (category, emoji) ->
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(110.dp)
                    .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = SoftShadow),
                shape    = RoundedCornerShape(20.dp),
                colors   = CardDefaults.cardColors(containerColor = SurfaceLight),
                border   = BorderStroke(1.dp, DividerColor),
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        modifier            = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(emoji, fontSize = 32.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            category,
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color      = TextPrimary,
                            maxLines   = 1,
                            overflow   = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}


// ── Recent searches ───────────────────────────────────────────────────────────

@Composable
private fun RecentSearchesList() {
    val recentSearches = listOf(
        "boAt Rockerz 450 Headphones",
        "iPhone 15 Pro Max",
        "Nike Air Max 270",
        "Samsung Galaxy S24 Ultra",
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        recentSearches.forEach { query ->
            Card(
                modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp), spotColor = SoftShadow),
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(containerColor = SurfaceLight),
                border   = BorderStroke(1.dp, DividerColor),
            ) {
                Row(
                    modifier          = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Default.History,
                        null,
                        tint     = TextMuted,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        query,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextPrimary,
                        modifier   = Modifier.weight(1f),
                    )
                    Icon(
                        Icons.Default.NorthWest,
                        null,
                        tint     = TextMuted,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}


// ── Popular brands grid ───────────────────────────────────────────────────────

@Composable
private fun PopularBrandsGrid() {
    val brands = listOf(
        "Apple" to "🍎", "Samsung" to "📱", "Nike" to "👟", "Sony" to "🎧",
        "boAt" to "⛵", "Adidas" to "👕", "OnePlus" to "📲", "Noise" to "⌚",
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(brands) { (name, emoji) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { /* search brand */ },
            ) {
                Surface(
                    shape    = CircleShape,
                    color    = SurfaceLight,
                    border   = BorderStroke(1.dp, DividerColor),
                    modifier = Modifier.size(60.dp).shadow(4.dp, CircleShape, spotColor = SoftShadow),
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(emoji, fontSize = 26.sp)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    name,
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary,
                )
            }
        }
    }
}
