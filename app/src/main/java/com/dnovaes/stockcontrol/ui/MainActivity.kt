package com.dnovaes.stockcontrol.ui

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dnovaes.stockcontrol.MainViewModel
import com.dnovaes.stockcontrol.R
import com.dnovaes.stockcontrol.common.StockNavHost
import com.dnovaes.stockcontrol.common.monitoring.log
import com.dnovaes.stockcontrol.common.ui.PrinterActivity
import com.dnovaes.stockcontrol.common.utils.QRCodeManager.generateQRCode
import com.dnovaes.stockcontrol.ui.theme.StockControlTheme
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

enum class State {
    START,
    PROCESSING,
    DONE,
    CAPTURING,
    IDLE
}

class MainActivity : PrinterActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val barCodeScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            //.setBarcodeFormats(Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E)
            //.setBarcodeFormats(Barcode.FORMAT_EAN_8, Barcode.FORMAT_EAN_13)
            .setBarcodeFormats(Barcode.FORMAT_CODE_39, Barcode.FORMAT_CODE_128)
            .build()
    )

    private var scannedValue: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scanPairedDevices()
        connectToPrinter()

        setContent {
            StockControlTheme {
                Surface {
                    val navController: NavHostController = rememberNavController()
                    StockNavHost(
                        context = this@MainActivity,
                        navHostController = navController,
                        bluetoothManager = bluetoothManager
                    )
                }
            }
        }
    }

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    @Composable
    fun MainScreen(state: State) {
        //val textRecognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }
        // Show loading indicator when isLoading is true
        when (state) {
            State.START, State.IDLE -> InitialScreen()
            State.DONE -> {
                Dialog(onDismissRequest = { }) {
                    Card(modifier = Modifier.fillMaxWidth(0.8f)) {
                        Text(
                            text = "Data extracted: $scannedValue",
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(top = 16.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Button(
                            onClick = { viewModel.resetState() },
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                        ) {
                            Text(text = "Done")
                        }
                    }
                }
            }
            State.PROCESSING -> LoadingIndicator()
            State.CAPTURING -> CameraScreen()
        }

    /*
        if (text.isNotEmpty()) {
            Dialog(onDismissRequest = { text = "" }) {
                Card(modifier = Modifier.fillMaxWidth(0.8f)) {
                    Text(
                        text = text,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Button(
                        onClick = { text = "" },
                        modifier = Modifier.align(Alignment.End).padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        Text(text = "Done")
                    }
                }
            }
        }
    */
    }

    @Composable
    fun InitialScreen() {
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                modifier = Modifier
                    .padding(6.dp)
                    .width(180.dp)
                    .height(44.dp),
                onClick = {
                    viewModel.startCapture()
                }
            ) {
                Text(
                    text = "Start Capture",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.background,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                modifier = Modifier
                    .padding(6.dp)
                    .width(240.dp)
                    .height(44.dp),
                onClick = {
                    scanPairedDevices()
                }
            ) {
                Text(
                    text = "Scan paired printer",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.background,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }

/*
            Button(
                modifier = Modifier
                    .padding(6.dp)
                    .width(240.dp)
                    .height(44.dp),
                onClick = {
                    connectToPrinter()
                }
            ) {
                Text(
                    text = "Connect with printer",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.background,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
*/

            var bitmap by remember { mutableStateOf<Bitmap?>(null) }
            Button(
                modifier = Modifier
                    .padding(6.dp)
                    .width(240.dp)
                    .height(44.dp),
                onClick = {
                    bitmap = generateQRCode("00000111", 100, 100)
                }
            ) {
                Text(
                    text = "Print label",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.background,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }

        }
    }

/*
    private fun generateQRCode1(data: String): Bitmap {
        val graphicsFactory = QRCodeGraphicsFactory()
        val qrCodeProcessor = QRCodeProcessor(data, ErrorCorrectionLevel.H, graphicsFactory = graphicsFactory)
        val typeNum = QRCodeProcessor.typeForDataAndECL(data, ErrorCorrectionLevel.M, QRCodeDataType.NUMBERS)
        val rawData = qrCodeProcessor.encode(typeNum)
        val computedSize = qrCodeProcessor.computeImageSize(4, 44, rawData) //dont touch it
        val graphics = graphicsFactory.newGraphicsSquare(computedSize)

        val qrCodeBuilder = QRCode.ofSquares()
            .withSize(4)
            .withInnerSpacing(1)
            .withBackgroundColor(Colors.WHITE)
            .build(data)

        //val qrCodeByteArray = qrCodeBuilder.render(graphics).getBytes()
        val qrCodeByteArray = qrCodeBuilder.render().getBytes()
        return BitmapFactory.decodeByteArray(qrCodeByteArray, 0, qrCodeByteArray.size)
    }
*/

    @Composable
    fun LoadingIndicator() {
        CircularProgressIndicator(
            modifier = Modifier
                .size(50.dp)
                //.align(Alignment.Center)
        )
    }

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    @Composable
    fun CameraScreen() {
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
                        .addOnSuccessListener { barcodes ->
                            imageProxy.close()
                            if (barcodes.isNotEmpty()) {
                                log("Scanned barcode: $barcodes")
                                scannedValue = barcodes.first().rawValue
                                viewModel.finishCapturing()
                            }
                        }
                        .addOnFailureListener {
                            // Handle any errors
                            imageProxy.close()
                            viewModel.finishCapturing()
                        }
                }
            }

            IconButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                onClick = {
                }
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
}

/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StockControlTheme {
        Greeting("Android")
    }
}
*/
