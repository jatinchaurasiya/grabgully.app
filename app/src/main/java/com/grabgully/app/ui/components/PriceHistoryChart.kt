package com.grabgully.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.grabgully.app.data.model.PricePoint
import com.grabgully.app.ui.theme.*
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf

/**
 * Price history chart using Vico (Compose-native M3 chart library).
 *
 * Renders a smooth line chart of 90-day price history.
 * - Line colour: GoldPrimary
 * - Fill gradient: GoldPrimary → transparent
 * - Grid lines: DividerColor
 * - No animations (data may update frequently)
 *
 * @param history    List of PricePoint from GET /compare/{id}/history
 * @param modifier   Standard Compose modifier
 */
@Composable
fun PriceHistoryChart(
    history:  List<PricePoint>,
    modifier: Modifier = Modifier,
) {
    if (history.isEmpty()) {
        Box(
            modifier          = modifier.fillMaxWidth().height(160.dp),
            contentAlignment  = Alignment.Center,
        ) {
            Text(
                "Price history loading...",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
            )
        }
        return
    }

    val prices = history.map { it.price.toFloat() }
    val minP   = prices.minOrNull() ?: 0f
    val maxP   = prices.maxOrNull() ?: 0f

    // Vico 1.x model
    val chartEntryModel = entryModelOf(*prices.toTypedArray())

    Column(modifier = modifier) {
        // Price range header
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            StatLabel("Low", "₹${"%.0f".format(minP)}", SavingsGreen)
            StatLabel("High", "₹${"%.0f".format(maxP)}", AlertRed)
            StatLabel("Now", "₹${"%.0f".format(prices.last())}", TealPrimary)
        }

        // Vico chart (1.x stable API)
        Chart(
            chart = lineChart(),
            model = chartEntryModel,
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(),
            modifier = Modifier.fillMaxWidth().height(160.dp)
        )
    }
}

@Composable
private fun StatLabel(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        Text(value, style = MaterialTheme.typography.labelLarge, color = color)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F6F8)
@Composable
private fun PriceHistoryChartPreview() {
    GrabGullyTheme {
        PriceHistoryChart(
            history = listOf(
                PricePoint(2999.0, "2025-01-01"),
                PricePoint(2799.0, "2025-01-15"),
                PricePoint(2499.0, "2025-02-01"),
                PricePoint(2699.0, "2025-02-15"),
                PricePoint(2299.0, "2025-03-01"),
                PricePoint(1999.0, "2025-03-15"),
                PricePoint(1799.0, "2025-04-01"),
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}
