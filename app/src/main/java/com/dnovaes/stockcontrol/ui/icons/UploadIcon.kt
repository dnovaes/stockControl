package com.dnovaes.stockcontrol.ui.icons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Composable
fun uploadIcon(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "upload",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(20f, 26.792f)
                quadToRelative(-0.542f, 0f, -0.917f, -0.375f)
                reflectiveQuadToRelative(-0.375f, -0.959f)
                verticalLineTo(12f)
                lineToRelative(-3.833f, 3.792f)
                quadToRelative(-0.417f, 0.375f, -0.937f, 0.375f)
                quadToRelative(-0.521f, 0f, -0.896f, -0.375f)
                quadToRelative(-0.375f, -0.417f, -0.375f, -0.959f)
                quadToRelative(0f, -0.541f, 0.375f, -0.916f)
                lineToRelative(6.041f, -6.042f)
                quadToRelative(0.209f, -0.208f, 0.438f, -0.292f)
                quadTo(19.75f, 7.5f, 20f, 7.5f)
                quadToRelative(0.25f, 0f, 0.479f, 0.083f)
                quadToRelative(0.229f, 0.084f, 0.438f, 0.292f)
                lineTo(27f, 13.958f)
                quadToRelative(0.375f, 0.375f, 0.375f, 0.896f)
                reflectiveQuadToRelative(-0.375f, 0.938f)
                quadToRelative(-0.417f, 0.375f, -0.958f, 0.375f)
                quadToRelative(-0.542f, 0f, -0.917f, -0.417f)
                lineTo(21.333f, 12f)
                verticalLineToRelative(13.458f)
                quadToRelative(0f, 0.584f, -0.395f, 0.959f)
                quadToRelative(-0.396f, 0.375f, -0.938f, 0.375f)
                close()
                moveTo(9.542f, 32.958f)
                quadToRelative(-1.042f, 0f, -1.834f, -0.791f)
                quadToRelative(-0.791f, -0.792f, -0.791f, -1.834f)
                verticalLineToRelative(-4.291f)
                quadToRelative(0f, -0.542f, 0.395f, -0.938f)
                quadToRelative(0.396f, -0.396f, 0.938f, -0.396f)
                quadToRelative(0.542f, 0f, 0.917f, 0.396f)
                reflectiveQuadToRelative(0.375f, 0.938f)
                verticalLineToRelative(4.291f)
                horizontalLineToRelative(20.916f)
                verticalLineToRelative(-4.291f)
                quadToRelative(0f, -0.542f, 0.375f, -0.938f)
                quadToRelative(0.375f, -0.396f, 0.917f, -0.396f)
                quadToRelative(0.583f, 0f, 0.958f, 0.396f)
                reflectiveQuadToRelative(0.375f, 0.938f)
                verticalLineToRelative(4.291f)
                quadToRelative(0f, 1.042f, -0.791f, 1.834f)
                quadToRelative(-0.792f, 0.791f, -1.834f, 0.791f)
                close()
            }
        }.build()
    }
}