package com.example.mor.nytnews.ui.common

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.example.mor.nytnews.R

@Composable
fun ExpandableText(
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = MaterialTheme.colorScheme.onSurface,
    text: String,
    collapsedMaxLine: Int = 6,
    showMoreText: String = stringResource(R.string.expandable_text_more),
    showMoreStyle: SpanStyle = SpanStyle(
        fontWeight = FontWeight.ExtraBold,
        textDecoration = TextDecoration.None,
        color = MaterialTheme.colorScheme.primary,
        background = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f),
    ),
) {
    var isExpanded by remember { mutableStateOf(false) }
    var clickable by remember { mutableStateOf(false) }
    var lastCharIndex by remember { mutableStateOf(0) }

    val textSpanStyle = style.toSpanStyle().copy(color = color)
    Box(
        modifier = Modifier.then(modifier)
    ) {
        val annotatedString = buildAnnotatedString {
            if (clickable) {
                if (isExpanded) {
                    withStyle(style = textSpanStyle) { append(text) }
                } else {
                    val adjustText =
                        text.substring(startIndex = 0, endIndex = lastCharIndex)
                            .dropLast(showMoreText.length)
                            .dropLastWhile { Character.isWhitespace(it) || it == '.' }
                    withStyle(style = textSpanStyle) { append(adjustText) }
                    pushStringAnnotation(tag = "MORE", annotation = showMoreText)
                    withStyle(style = showMoreStyle) { append(showMoreText) }
                }
            } else {
                withStyle(style = textSpanStyle) { append(text) }
            }
        }
        ClickableText(modifier = textModifier
            .fillMaxWidth()
            .animateContentSize(),
            text = annotatedString,
            maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLine,
            onTextLayout = { textLayoutResult ->
                if (!isExpanded && textLayoutResult.hasVisualOverflow) {
                    clickable = true
                    lastCharIndex = textLayoutResult.getLineEnd(collapsedMaxLine - 1)
                }
            },
            style = style,
            onClick = {
                annotatedString.getStringAnnotations("MORE", it, it).firstOrNull()
                    ?.let { isExpanded = !isExpanded }
            })
    }
}