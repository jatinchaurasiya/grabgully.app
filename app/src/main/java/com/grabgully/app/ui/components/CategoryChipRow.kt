package com.grabgully.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grabgully.app.ui.theme.*

data class Category(val id: String, val label: String, val emoji: String = "")

val DefaultCategories = listOf(
    Category("all",         "All Deals"),
    Category("electronics", "Electronics"),
    Category("fashion",     "Fashion"),
    Category("home",        "Ghar"),
    Category("beauty",      "Beauty"),
    Category("sports",      "Sports"),
    Category("grocery",     "Grocery"),
)

/**
 * Horizontal scrollable category filter row (v4.0 — Teal Light Theme).
 *
 * Selected chip: TealPrimary bg + Color.White text.
 * Unselected chip: InactiveChipBg + InactiveChipText, no border.
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
        contentPadding      = PaddingValues(horizontal = 4.dp),
        verticalAlignment   = Alignment.CenterVertically,
    ) {
        items(categories, key = { it.id }) { cat ->
            val selected = cat.id == selectedCategory
            FilterChip(
                selected = selected,
                onClick  = { onCategorySelect(cat.id) },
                label    = {
                    Text(
                        text       = cat.label,
                        fontSize   = 14.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        fontFamily = InterFamily,
                        color      = if (selected) Color.White else InactiveChipText,
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor         = InactiveChipBg,
                    selectedContainerColor = TealPrimary,
                    labelColor             = InactiveChipText,
                    selectedLabelColor     = Color.White,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled             = true,
                    selected            = selected,
                    borderColor         = InactiveChipBg,
                    selectedBorderColor = TealPrimary,
                    borderWidth         = 0.dp,
                ),
                shape = CircleShape,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F6F8)
@Composable
private fun CategoryChipRowPreview() {
    GrabGullyTheme {
        CategoryChipRow(selectedCategory = "electronics")
    }
}
