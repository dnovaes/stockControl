package com.dnovaes.stockcontrol.common.utils

import android.graphics.Bitmap
import android.graphics.Matrix

fun rotateBitmap(source: Bitmap, rotationDegrees: Float): Bitmap {
    val matrix = Matrix().apply {
        postRotate(rotationDegrees)
    }
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
}