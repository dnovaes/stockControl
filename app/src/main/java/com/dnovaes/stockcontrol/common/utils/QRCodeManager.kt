package com.dnovaes.stockcontrol.common.utils

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

object QRCodeManager {

    fun generateQRCode(
        data: String,
        width: Int,
        height: Int
    ): Bitmap {
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(
            data,
            BarcodeFormat.QR_CODE,
            width,
            height,
            mapOf(EncodeHintType.MARGIN to 0)
        )

        val matrixWidth = bitMatrix.width
        val matrixHeight = bitMatrix.height
        val bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.RGB_565)

        for (x in 0 until matrixWidth) {
            for (y in 0 until matrixHeight) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        return bitmap
    }

}
