package com.example.setchat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Constraints
import com.example.setchat.data.local.Message

@Composable
fun ChatBubble(
    modifier: Modifier = Modifier,
    message: Message,
    isDarkTheme: Boolean = true,
    showTimestamp: Boolean = true,
    showSenderName: Boolean = true,
    showDeliveryIcon: Boolean = false
) {
    val mineBubble = if (isDarkTheme) Color(0xFF134B78) else Color(0xFFD9FDD3)
    val otherBubble = if (isDarkTheme) Color(0xFF1D232A) else Color.White
    val mainText = if (isDarkTheme) Color(0xFFF2F7FD) else Color(0xFF111B21)
    val secondaryText = if (isDarkTheme) Color(0xFF8EA2B6) else Color(0xFF667781)
    val senderText = if (isDarkTheme) Color(0xFF5FB4FF) else Color(0xFF0B8F68)
    val bubbleVerticalPadding = 8.dp
    val bubbleHorizontalPadding = 11.dp
    val bodyTextStyle = TextStyle(color = mainText, fontSize = 17.sp, lineHeight = 22.sp)
    val metaTextStyle = TextStyle(color = secondaryText, fontSize = 12.sp)
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = Modifier
            .then(modifier)
            .background(
                color = if (message.isMine) mineBubble else otherBubble,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = bubbleHorizontalPadding, vertical = bubbleVerticalPadding)
    ) {
        val senderNameMaxWidth = maxWidth * 0.72f
        val contentMaxWidthPx = with(density) { maxWidth.roundToPx() }
        val inlineGapPx = with(density) { 8.dp.roundToPx() }
        val overlayGapPx = with(density) { 6.dp.roundToPx() }
        val timestampText = message.timestamp
        val hasTimestamp = showTimestamp && timestampText.isNotBlank()
        val timestampWidthPx = if (hasTimestamp) {
            textMeasurer.measure(
                text = AnnotatedString(timestampText),
                style = metaTextStyle
            ).size.width
        } else {
            0
        }
        val singleLineLayout = if (message.text.isNotBlank()) {
            textMeasurer.measure(
                text = AnnotatedString(message.text),
                style = bodyTextStyle,
                constraints = Constraints(
                    maxWidth = (contentMaxWidthPx - timestampWidthPx - inlineGapPx).coerceAtLeast(1)
                )
            )
        } else {
            null
        }
        val inlineTimestamp = hasTimestamp &&
            singleLineLayout != null &&
            singleLineLayout.lineCount == 1 &&
            singleLineLayout.size.width + timestampWidthPx + inlineGapPx <= contentMaxWidthPx
        val trailingReserve = if (hasTimestamp && !inlineTimestamp) {
            with(density) { (timestampWidthPx + overlayGapPx).toDp() }
        } else {
            0.dp
        }

        Column {
            if (!message.isMine && showSenderName) {
                Text(
                    text = "∼${message.senderName}",
                    color = senderText,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = senderNameMaxWidth)
                )
                Spacer(modifier = Modifier.height(2.dp))
            }

            if (message.text.isNotBlank()) {
                if (inlineTimestamp) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = message.text,
                            style = bodyTextStyle
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = timestampText,
                            style = metaTextStyle
                        )
                    }
                } else {
                    Box {
                        Text(
                            text = message.text,
                            style = bodyTextStyle,
                            modifier = if (hasTimestamp) Modifier.padding(end = trailingReserve) else Modifier
                        )
                        if (hasTimestamp) {
                            Text(
                                text = timestampText,
                                style = metaTextStyle,
                                modifier = Modifier.align(Alignment.BottomEnd)
                            )
                        }
                    }
                }
            }

            if (showTimestamp && message.timestamp.isNotBlank() && message.text.isBlank()) {
                Spacer(modifier = Modifier.height(3.dp))
                Box(modifier = Modifier.align(Alignment.End)) {
                    Text(
                        text = message.timestamp,
                        style = metaTextStyle
                    )
                }
            }

            if (showDeliveryIcon) {
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}
