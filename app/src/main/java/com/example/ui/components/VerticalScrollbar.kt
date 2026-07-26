package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import kotlinx.coroutines.launch

fun Modifier.verticalScrollbar(
    state: LazyListState,
    color: Color = CyberGreen,
    width: Dp = 8.dp
): Modifier = composed {
    var isDragging by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val targetAlpha = if (state.isScrollInProgress || isDragging) 0.95f else 0.6f
    val alpha by animateFloatAsState(targetValue = targetAlpha, label = "scrollbarAlpha")

    this
        .pointerInput(state) {
            val totalItems = state.layoutInfo.totalItemsCount
            if (totalItems <= 0) return@pointerInput

            detectTapGestures { offset ->
                val trackHeight = size.height.toFloat()
                if (trackHeight > 0f) {
                    val clickRatio = (offset.y / trackHeight).coerceIn(0f, 1f)
                    val targetIndex = (clickRatio * (totalItems - 1)).toInt().coerceIn(0, totalItems - 1)
                    coroutineScope.launch {
                        state.scrollToItem(targetIndex)
                    }
                }
            }
        }
        .pointerInput(state) {
            val totalItems = state.layoutInfo.totalItemsCount
            if (totalItems <= 0) return@pointerInput

            detectDragGestures(
                onDragStart = { isDragging = true },
                onDragEnd = { isDragging = false },
                onDragCancel = { isDragging = false }
            ) { change, dragAmount ->
                change.consume()
                val trackHeight = size.height.toFloat()
                if (trackHeight > 0f) {
                    val currentRatio = (change.position.y / trackHeight).coerceIn(0f, 1f)
                    val targetIndex = (currentRatio * (totalItems - 1)).toInt().coerceIn(0, totalItems - 1)
                    coroutineScope.launch {
                        state.scrollToItem(targetIndex)
                    }
                }
            }
        }
        .drawWithContent {
            drawContent()

            val firstVisibleElementIndex = state.layoutInfo.visibleItemsInfo.firstOrNull()?.index
            val elementCount = state.layoutInfo.totalItemsCount

            if (elementCount > 0 && firstVisibleElementIndex != null) {
                val visibleItemCount = state.layoutInfo.visibleItemsInfo.size
                val viewportHeight = size.height

                val scrollbarHeight = (viewportHeight * (visibleItemCount.toFloat() / elementCount.toFloat()))
                    .coerceIn(44.dp.toPx(), viewportHeight)
                val denominator = (elementCount - visibleItemCount).coerceAtLeast(1).toFloat()
                val scrollbarOffsetY = (viewportHeight - scrollbarHeight) * (firstVisibleElementIndex.toFloat() / denominator)

                val thumbColor = if (isDragging) CyberCyan else color

                // Track background
                drawRoundRect(
                    color = Color.Gray.copy(alpha = 0.25f),
                    topLeft = Offset(size.width - width.toPx() - 2.dp.toPx(), 0f),
                    size = Size(width.toPx() + 2.dp.toPx(), viewportHeight),
                    cornerRadius = CornerRadius(width.toPx() / 2, width.toPx() / 2)
                )

                // Scrollbar thumb
                drawRoundRect(
                    color = thumbColor.copy(alpha = alpha),
                    topLeft = Offset(size.width - width.toPx() - 2.dp.toPx(), scrollbarOffsetY.coerceIn(0f, viewportHeight - scrollbarHeight)),
                    size = Size(width.toPx() + 2.dp.toPx(), scrollbarHeight),
                    cornerRadius = CornerRadius(width.toPx() / 2, width.toPx() / 2)
                )
            }
        }
}

fun Modifier.verticalScrollbar(
    state: ScrollState,
    color: Color = CyberGreen,
    width: Dp = 8.dp
): Modifier = composed {
    var isDragging by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val targetAlpha = if (state.isScrollInProgress || isDragging) 0.95f else 0.6f
    val alpha by animateFloatAsState(targetValue = targetAlpha, label = "scrollbarAlpha")

    this
        .pointerInput(state) {
            val maxScroll = state.maxValue
            if (maxScroll <= 0) return@pointerInput

            detectTapGestures { offset ->
                val trackHeight = size.height.toFloat()
                if (trackHeight > 0f) {
                    val clickRatio = (offset.y / trackHeight).coerceIn(0f, 1f)
                    val targetScroll = (clickRatio * maxScroll).toInt().coerceIn(0, maxScroll)
                    coroutineScope.launch {
                        state.scrollTo(targetScroll)
                    }
                }
            }
        }
        .pointerInput(state) {
            val maxScroll = state.maxValue
            if (maxScroll <= 0) return@pointerInput

            detectDragGestures(
                onDragStart = { isDragging = true },
                onDragEnd = { isDragging = false },
                onDragCancel = { isDragging = false }
            ) { change, dragAmount ->
                change.consume()
                val trackHeight = size.height.toFloat()
                if (trackHeight > 0f) {
                    val currentRatio = (change.position.y / trackHeight).coerceIn(0f, 1f)
                    val targetScroll = (currentRatio * maxScroll).toInt().coerceIn(0, maxScroll)
                    coroutineScope.launch {
                        state.scrollTo(targetScroll)
                    }
                }
            }
        }
        .drawWithContent {
            drawContent()

            val maxScroll = state.maxValue
            if (maxScroll > 0) {
                val viewportHeight = size.height
                val totalContentHeight = viewportHeight + maxScroll
                val scrollbarHeight = (viewportHeight * (viewportHeight / totalContentHeight))
                    .coerceIn(44.dp.toPx(), viewportHeight)
                val scrollbarOffsetY = (state.value.toFloat() / maxScroll.toFloat()) * (viewportHeight - scrollbarHeight)

                val thumbColor = if (isDragging) CyberCyan else color

                // Track background
                drawRoundRect(
                    color = Color.Gray.copy(alpha = 0.25f),
                    topLeft = Offset(size.width - width.toPx() - 2.dp.toPx(), 0f),
                    size = Size(width.toPx() + 2.dp.toPx(), viewportHeight),
                    cornerRadius = CornerRadius(width.toPx() / 2, width.toPx() / 2)
                )

                // Scrollbar thumb
                drawRoundRect(
                    color = thumbColor.copy(alpha = alpha),
                    topLeft = Offset(size.width - width.toPx() - 2.dp.toPx(), scrollbarOffsetY.coerceIn(0f, viewportHeight - scrollbarHeight)),
                    size = Size(width.toPx() + 2.dp.toPx(), scrollbarHeight),
                    cornerRadius = CornerRadius(width.toPx() / 2, width.toPx() / 2)
                )
            }
        }
}

