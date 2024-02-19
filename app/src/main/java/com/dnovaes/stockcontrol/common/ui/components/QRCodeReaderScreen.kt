package com.dnovaes.stockcontrol.common.ui.components

import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.dnovaes.stockcontrol.R
import com.dnovaes.stockcontrol.common.monitoring.log
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

private val barCodeScanner = BarcodeScanning.getClient(
    BarcodeScannerOptions.Builder()
        //.setBarcodeFormats(Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E)
        //.setBarcodeFormats(Barcode.FORMAT_EAN_8, Barcode.FORMAT_EAN_13)
        //.setBarcodeFormats(Barcode.FORMAT_CODE_39, Barcode.FORMAT_CODE_128)
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
)


@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun QRCodeReaderScreen(
    onFinishCapture: (String) -> Unit
) {
    val executor = remember { Executors.newSingleThreadExecutor() }
    val lifecycleOwner = LocalLifecycleOwner.current

    val context = LocalContext.current
    val previewView: PreviewView = remember { PreviewView(context) }

    val cameraController = remember { LifecycleCameraController(context) }
    cameraController.bindToLifecycle(lifecycleOwner)
    cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    previewView.controller = cameraController

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        cameraController.setImageAnalysisAnalyzer(executor) { imageProxy ->
            imageProxy.image?.let { image ->
                val img = InputImage.fromMediaImage(
                    image,
                    imageProxy.imageInfo.rotationDegrees
                )

                barCodeScanner.process(img)
                    .addOnSuccessListener { codes ->
                        imageProxy.close()
                        if (codes.isNotEmpty()) {
                            log("Scanned barcode: $codes")
                            codes.first().rawValue?.let { code ->
                                onFinishCapture(code)
                            }
                        }
                    }
                    .addOnFailureListener {
                        // Handle any errors
                        imageProxy.close()
                        //viewModel.finishCapturing()
                    }
            }
        }

        IconButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            onClick = { }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_camera_24),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(54.dp)
            )
        }
    }
}
