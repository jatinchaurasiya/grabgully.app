package com.grabgully.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.grabgully.app.ui.theme.*

/**
 * Green % OFF savings badge shown on DealCard.
 * Matches PRD token: SavingsBg + SavingsGreen border + SavingsGreen text.
 */
@Composable
fun SavingsBadge(
    percent:  Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = SavingsBg,
        modifier = modifier.border(
            width = 0.5.dp,
            color = SavingsGreen.copy(alpha = 0.7f),
            shape = RoundedCornerShape(4.dp),
        ),
    ) {
        Text(
            text     = "$percent% OFF",
            style    = BadgeTextStyle,
            color    = SavingsGreen,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF111119)
@Composable
private fun SavingsBadgePreview() {
    GrabGullyTheme {
        SavingsBadge(percent = 57)
    }
}
