@file:OptIn(ExperimentalTextApi::class)

package com.dreamyducks.navcook.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.dreamyducks.navcook.R


val NotoSans = FontFamily(
    Font(
        R.font.notosans,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(700)
        )
    )
)
val NewsReader = FontFamily(
    Font(R.font.newsreader_extrabold, FontWeight.ExtraBold)
)
val Ultra = FontFamily(
    Font(R.font.ultra_regular, FontWeight.Bold)
)

val Inter = FontFamily(
    Font(
        R.font.inter,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(500)
        )
    )
)

// Set of Material typography styles to start with

val baseline = Typography()
val Typography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = NotoSans),
    displayMedium = baseline.displayMedium.copy(fontFamily = NotoSans),
    displaySmall = baseline.displaySmall.copy(fontFamily = NotoSans),

    headlineLarge = TextStyle(
        fontFamily = Ultra,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp
    ),

    headlineMedium = baseline.headlineMedium.copy(fontFamily = NotoSans),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = NotoSans),

    titleLarge = TextStyle(
        fontFamily = Inter,
        fontSize = 28.sp,
        letterSpacing = 1.sp,
        lineHeight = 2.em,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.None
        ),
    ),

    titleMedium = TextStyle(
        fontFamily = NewsReader,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp
    ),

    titleSmall = baseline.titleSmall.copy(fontFamily = NotoSans),

    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    bodyMedium = baseline.bodyMedium.copy(fontFamily = NotoSans),
    bodySmall = baseline.bodySmall.copy(fontFamily = NotoSans),
    labelLarge = baseline.labelLarge.copy(fontFamily = NotoSans),
    labelMedium = baseline.labelMedium.copy(fontFamily = NotoSans),
    labelSmall = baseline.labelSmall.copy(fontFamily = NotoSans)
)