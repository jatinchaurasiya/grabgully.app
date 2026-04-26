package com.grabgully.app.ui.screens.search

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
        containerColor = ObsidianBlack,
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
                            CircularProgressIndicator(color = GoldPrimary)
                        }
                    }
                    uiState.results.isEmpty() && uiState.query.isNotBlank() && !uiState.isLoading -> {
                        Column(
                            Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(Icons.Default.SearchOff, null, tint = TextMuted, modifier = Modifier.size(64.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("'${uiState.query}' nahi mila", color = TextSecondary, style = MaterialTheme.typography.bodyLarge)
                            Text("Doosra keyword try karo", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    uiState.results.isEmpty() -> {
                        // Initial hint state
                        SearchHintState()
                    }
                    else -> {
                        LazyColumn(
                            contentPadding    = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
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
    Surface(color = SurfaceDeep, tonalElevation = 0.dp) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, "Back", tint = TextPrimary)
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
                        tint = if (urlMode) GoldPrimary else TextMuted,
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
                    focusedBorderColor   = GoldPrimary,
                    unfocusedBorderColor = DividerColor,
                    focusedTextColor     = TextPrimary,
                    unfocusedTextColor   = TextPrimary,
                    cursorColor          = GoldPrimary,
                ),
                shape = RoundedCornerShape(12.dp),
            )
            // URL mode toggle
            IconButton(onClick = onUrlToggle) {
                Icon(
                    if (urlMode) Icons.Default.TextFields else Icons.Default.Link,
                    contentDescription = "Toggle URL mode",
                    tint = if (urlMode) GoldPrimary else TextMuted,
                )
            }
        }
    }
}

@Composable
private fun PlatformFilterRow(
    selected:        String?,
    onPlatformSelect: (String?) -> Unit,
) {
    val platforms = listOf("All", "Amazon", "Flipkart", "Myntra", "Meesho", "Ajio")
    LazyRow(
        contentPadding      = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier            = Modifier.padding(vertical = 8.dp),
    ) {
        items(platforms) { p ->
            val isSelected = (p == "All" && selected == null) || p.lowercase() == selected
            FilterChip(
                selected = isSelected,
                onClick  = { onPlatformSelect(if (p == "All") null else p.lowercase()) },
                label    = { Text(p, color = if (isSelected) ObsidianBlack else TextSecondary) },
                colors   = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GoldPrimary,
                ),
                shape    = androidx.compose.foundation.shape.CircleShape,
            )
        }
    }
}

@Composable
private fun SearchHintState() {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Default.TravelExplore, null, tint = GoldPrimary, modifier = Modifier.size(80.dp))
        Spacer(Modifier.height(16.dp))
        Text("Koi bhi product search karo", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        Text("Ya Amazon/Flipkart URL paste karo\nham price compare kar denge!", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}
