package com.casecode.pos.feature.setting.printer


import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation



class IpAddressTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Add dots after every three digits (max 3 dots)
        val transformedText = buildString {
            var dotCount = 0
            for (i in text.text.indices) {
                append(text.text[i])
                if ((i + 1) % 3 == 0 && dotCount < 3 && i < text.text.length - 1) {
                    append('.')
                    dotCount++
                }
            }
        }

        // Calculate cursor position mapping
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return transformedText.length.coerceAtMost(offset + transformedText.count { it == '.' })
            }

            override fun transformedToOriginal(offset: Int): Int {
                return offset - (offset / 4).coerceAtMost(3)
            }
        }

        return TransformedText(AnnotatedString(transformedText), offsetMapping)
    }
}
fun isValidIpAddress(ipAddress: String): Boolean {
    val segments = ipAddress.split(".")
    // Check if we have 4 segments (or less while typing)
    if (segments.size > 4) {
        return false
    }

    for (segment in segments) {
        // Allow empty segments for in-progress typing
        if (segment.isEmpty()) continue

        // Check if the segment is a valid number and in the range 0-255
        val segmentValue = segment.toIntOrNull() ?: -1
        if (segmentValue !in 0..255) {
            return false
        }
    }
    return true
}