package com.grabgully.app.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grabgully.app.ui.theme.*

/**
 * Green % OFF savings badge (v3.0 — matches mockup `bg-tertiary/10 text-tertiary`).
 * Tertiary-tinted surface with tertiary text, compact 10sp bold.
 */
@Composable
fun SavingsBadge(
    percent:  Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = SavingsGreen.copy(alpha = 0.1f),
        modifier = modifier,
    ) {
        Text(
            text       = "$percent% OFF",
            fontSize   = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PlusJakartaSansFamily,
            color      = SavingsGreen,
            modifier   = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F6F8)
@Composable
private fun SavingsBadgePreview() {
    GrabGullyTheme {
        SavingsBadge(percent = 57)
    }
}
