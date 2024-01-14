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
fun printerFilledIcon(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "print",
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
                moveTo(29.75f, 11.917f)
                horizontalLineToRelative(-19.5f)
                verticalLineTo(6.625f)
                quadToRelative(0f, -0.542f, 0.375f, -0.917f)
                reflectiveQuadToRelative(0.917f, -0.375f)
                horizontalLineToRelative(16.916f)
                quadToRelative(0.542f, 0f, 0.917f, 0.375f)
                reflectiveQuadToRelative(0.375f, 0.917f)
                close()
                moveToRelative(0.625f, 7.875f)
                quadToRelative(0.542f, 0f, 0.937f, -0.375f)
                quadToRelative(0.396f, -0.375f, 0.396f, -0.917f)
                reflectiveQuadToRelative(-0.396f, -0.938f)
                quadToRelative(-0.395f, -0.395f, -0.937f, -0.395f)
                quadToRelative(-0.5f, 0f, -0.896f, 0.395f)
                quadToRelative(-0.396f, 0.396f, -0.396f, 0.938f)
                quadToRelative(0f, 0.542f, 0.396f, 0.917f)
                reflectiveQuadToRelative(0.896f, 0.375f)
                close()
                moveToRelative(-17.5f, 12.333f)
                horizontalLineToRelative(14.25f)
                verticalLineToRelative(-7.583f)
                horizontalLineToRelative(-14.25f)
                verticalLineToRelative(7.583f)
                close()
                moveToRelative(0f, 2.625f)
                quadToRelative(-1.083f, 0f, -1.854f, -0.771f)
                quadToRelative(-0.771f, -0.771f, -0.771f, -1.854f)
                verticalLineToRelative(-4.417f)
                horizontalLineTo(4.958f)
                quadToRelative(-0.541f, 0f, -0.916f, -0.354f)
                reflectiveQuadToRelative(-0.375f, -0.937f)
                verticalLineToRelative(-8.709f)
                quadToRelative(0f, -1.875f, 1.291f, -3.166f)
                quadTo(6.25f, 13.25f, 8.125f, 13.25f)
                horizontalLineToRelative(23.75f)
                quadToRelative(1.875f, 0f, 3.167f, 1.292f)
                quadToRelative(1.291f, 1.291f, 1.291f, 3.166f)
                verticalLineToRelative(8.709f)
                quadToRelative(0f, 0.583f, -0.375f, 0.937f)
                quadToRelative(-0.375f, 0.354f, -0.916f, 0.354f)
                horizontalLineTo(29.75f)
                verticalLineToRelative(4.417f)
                quadToRelative(0f, 1.083f, -0.771f, 1.854f)
                quadToRelative(-0.771f, 0.771f, -1.854f, 0.771f)
                close()
            }
        }.build()
    }
}
