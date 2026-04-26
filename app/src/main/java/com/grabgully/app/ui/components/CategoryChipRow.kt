package com.grabgully.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.grabgully.app.ui.theme.*

data class Category(val id: String, val label: String, val emoji: String = "")

val DefaultCategories = listOf(
    Category("all",         "All Deals",   "🔥"),
    Category("electronics", "Electronics", "📱"),
    Category("fashion",     "Fashion",     "👗"),
    Category("home",        "Ghar",        "🏠"),
    Category("beauty",      "Beauty",      "💄"),
    Category("sports",      "Sports",      "🏋️"),
    Category("grocery",     "Grocery",     "🛒"),
)

/**
 * Horizontal scrollable category filter row.
 * Selected chip: GoldPrimary bg + ObsidianBlack text.
 * Unselected chip: SurfaceRaised bg + TextSecondary text.
 */
@Composable
fun CategoryChipRow(
    categories:       List<Category> = DefaultCategories,
    selectedCategory: String         = "all",
    onCategorySelect: (String) -> Unit = {},
    modifier:         Modifier        = Modifier,
) {
    LazyRow(
        modifier            = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding      = PaddingValues(horizontal = 16.dp),
        verticalAlignment   = Alignment.CenterVertically,
    ) {
        items(categories, key = { it.id }) { cat ->
            val selected = cat.id == selectedCategory
            FilterChip(
                selected = selected,
                onClick  = { onCategorySelect(cat.id) },
                label    = {
                    Text(
                        text  = "${cat.emoji} ${cat.label}".trim(),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selected) ObsidianBlack else TextSecondary,
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor         = SurfaceRaised,
                    selectedContainerColor = GoldPrimary,
                    labelColor             = TextSecondary,
                    selectedLabelColor     = ObsidianBlack,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled       = true,
                    selected      = selected,
                    borderColor   = DividerColor,
                    selectedBorderColor = GoldPrimary,
                    borderWidth   = 0.dp,
                ),
                shape = androidx.compose.foundation.shape.CircleShape,
            )
        }
    }
}

@Suppress("UNUSED_EXPRESSION")
private val MaterialTheme get() = androidx.compose.material3.MaterialTheme

@Preview(showBackground = true, backgroundColor = 0xFF08080F)
@Composable
private fun CategoryChipRowPreview() {
    GrabGullyTheme {
        CategoryChipRow(selectedCategory = "electronics")
    }
}
