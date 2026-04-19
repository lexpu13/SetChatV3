package com.example.setchat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.setchat.viewmodel.FakeStatusBarSettings

@Composable
fun FakeStatusBar(
    isDarkTheme: Boolean,
    settings: FakeStatusBarSettings,
    modifier: Modifier = Modifier,
    applySystemInsets: Boolean = true
) {
    val backgroundColor = Color.Transparent
    val foregroundColor = if (isDarkTheme) Color(0xFFEAF2FF) else Color(0xFF0F172A)
    val containerModifier = if (applySystemInsets) {
        modifier.fillMaxWidth().statusBarsPadding()
    } else {
        modifier.fillMaxWidth()
    }

    Row(
        modifier = containerModifier
            .background(backgroundColor)
            .height(30.dp)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = settings.time.ifBlank { "21:18" },
            color = foregroundColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        )
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = settings.operator.ifBlank { "Operateur" },
                color = foregroundColor,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "  ${settings.network.ifBlank { "5G" }}",
                color = foregroundColor,
                fontSize = 11.sp
            )
            Text(
                text = "  ${settings.batteryLevel.coerceIn(0, 100)}%",
                color = foregroundColor,
                fontSize = 11.sp
            )
            BatteryPill(
                level = settings.batteryLevel.coerceIn(0, 100),
                tint = foregroundColor,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}

@Composable
private fun BatteryPill(
    level: Int,
    tint: Color,
    modifier: Modifier = Modifier
) {
    val clampedLevel = level.coerceIn(0, 100)
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(22.dp)
                .height(10.dp)
                .border(1.dp, tint, RoundedCornerShape(3.dp))
                .padding(1.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((clampedLevel / 100f).coerceIn(0f, 1f))
                    .height(8.dp)
                    .background(tint, RoundedCornerShape(2.dp))
            )
        }
        Box(
            modifier = Modifier
                .padding(start = 1.dp)
                .width(2.dp)
                .height(6.dp)
                .background(tint, RoundedCornerShape(1.dp))
        )
    }
}
