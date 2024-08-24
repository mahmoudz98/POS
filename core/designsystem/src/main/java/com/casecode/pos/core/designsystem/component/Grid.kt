package com.casecode.pos.core.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout

@Composable
fun VerticalGrid(
    modifier: Modifier = Modifier,
    columns: Int = 3,
    content: @Composable () -> Unit,
) {
    Layout(
        content = content,
        modifier = modifier,
    ) { measurables, constraints ->
        val itemWidth = constraints.maxWidth / columns
        // Keep given height constraints, but set an exact width
        val itemConstraints =
            constraints.copy(
                minWidth = itemWidth,
                maxWidth = itemWidth,
            )
        // Measure each item with these constraints
        val placeables = measurables.map { it.measure(itemConstraints) }
        // Track each columns height so we can calculate the overall height
        val columnHeights = Array(columns) { 0 }
        placeables.forEachIndexed { index, placeable ->
            val column = index % columns
            columnHeights[column] += placeable.height
        }
        val height =
            (columnHeights.maxOrNull() ?: constraints.minHeight)
                .coerceAtMost(constraints.maxHeight)
        layout(
            width = constraints.maxWidth,
            height = height,
        ) {
            // Track the Y co-ord per column we have placed up to
            val columnY = Array(columns) { 0 }
            placeables.forEachIndexed { index, placeable ->
                val column = index % columns
                placeable.placeRelative(
                    x = column * itemWidth,
                    y = columnY[column],
                )
                columnY[column] += placeable.height
            }
        }
    }
}

@Composable
fun HorizontalGrid(
    modifier: Modifier = Modifier,
    columns: Int = 3,
    content: @Composable () -> Unit,
) {
    Layout(
        content = content,
        modifier = modifier,
    ) { measurables, constraints ->

        val itemHeight = constraints.maxHeight / columns

        val itemConstraints =
            constraints.copy(
                minHeight = itemHeight,
                maxHeight = itemHeight,
            )
        val placeables = measurables.map { it.measure(itemConstraints) }
        val columnWidths = Array(columns) { 0 }
        placeables.forEachIndexed { index, placeable ->
            val column = index % columns
            columnWidths[column] += placeable.width
        }
        val width =
            (columnWidths.maxOrNull() ?: constraints.minWidth)
                .coerceAtMost(constraints.maxWidth)
        layout(
            width = width,
            height = constraints.maxHeight,
        ) {
            val columnX = Array(columns) { 0 }
            placeables.forEachIndexed { index, placeable ->
                val column = index % columns
                placeable.placeRelative(
                    x = columnX[column],
                    y = column * itemHeight,
                )
                columnX[column] += placeable.width
            }
        }
    }
}