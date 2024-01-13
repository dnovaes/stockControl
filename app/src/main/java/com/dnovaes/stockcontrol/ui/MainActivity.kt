package com.dnovaes.stockcontrol.ui

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dnovaes.stockcontrol.MainViewModel
import com.dnovaes.stockcontrol.R
import com.dnovaes.stockcontrol.common.extensions.navigateSingleTopTo
import com.dnovaes.stockcontrol.common.monitoring.log
import com.dnovaes.stockcontrol.common.ui.FullScreenAlert
import com.dnovaes.stockcontrol.common.ui.PrinterActivity
import com.dnovaes.stockcontrol.features.addproduct.ui.AddProductPage
import com.dnovaes.stockcontrol.ui.pages.LandingPage
import com.dnovaes.stockcontrol.ui.theme.StockControlTheme
import com.dnovaes.stockcontrol.features.addproduct.viewmodel.AddViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.smarttoolfactory.screenshot.ScreenshotBox
import com.smarttoolfactory.screenshot.rememberScreenshotState
import java.util.concurrent.Executors

enum class State {
    INITIAL,
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
        setContent {
            StockControlTheme {
                Surface {
                    val navController: NavHostController = rememberNavController()
                    StockNavHost(navController)
                }
            }
        }
    }

    @Composable
    fun StockNavHost(navHostController: NavHostController) {
        NavHost(
            navController = navHostController,
            startDestination = "LandingPage",
        ) {
            composable(route = "LandingPage") {
                LandingPage(
                    onClickAdd = {
                        navHostController.navigateSingleTopTo("AddProductPage")
                    },
                    onClickManage = { }
                )
            }
            composable(route = "AddProductPage") {
                AddProductPage(
                    context = this@MainActivity,
                    viewModel = AddViewModel(),
                    onBackPressed =  {
                        navHostController.popBackStack()
                    },
                    onFinishRegistration = {
                        navHostController.navigateSingleTopTo("SuccessfulAddRegistrationPage")
                    }
                )
            }
            composable(route = "SuccessfulAddRegistrationPage") {
                FullScreenAlert(
                    headerIcon = Icons.Filled.ThumbUp,
                    title = R.string.add_success_page_title,
                    subtitle = null,
                    positiveButtonLabel = R.string.add_success_page_positive_bt_label,
                    negativeButtonLabel = R.string.add_success_page_negative_bt_label,
                    onPositiveButtonClick = {

                    },
                    onNegativeButtonClick = {
                        navHostController.popBackStack()
                    }
                )
            }
/*
            composable(route = "ManageProductPage") {
                LandingPage()
            }
*/
        }
    }

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    @Composable
    fun MainScreen(state: State) {
        //val textRecognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }
        // Show loading indicator when isLoading is true
        when (state) {
            State.INITIAL, State.IDLE -> InitialScreen()
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
                    printPairedDevices()
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

            bitmap?.let {
                val screenshotState = rememberScreenshotState()
                screenshotState.bitmapState.value?.let {
                    screenshotState.bitmapState.value = null
                    printWithNiimbotClient(it)
                }
                ScreenshotBox(screenshotState = screenshotState) {
                    PreviewPrintLabel(it) {
                        screenshotState.capture()
                    }
                }
            }
        }
    }

    private fun generateQRCode(
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

        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        return bitmap
    }

    @Composable
    fun BitmapImage(bitmap: Bitmap, size: Dp) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "QR code image",
            modifier = Modifier.size(size)
        )
    }

    @Composable
    fun PreviewPrintLabel(image: Bitmap, onClick: () -> Unit) {
        Box(
            modifier = Modifier
                /*
                .border(
                    width = 1.dp,
                    color = Color(android.graphics.Color.BLACK),
                    shape = RectangleShape
                )
*/
                .width(160.dp)
                .height(80.dp)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null, // Remove the visual indication
                ) {
                    onClick.invoke()
                },
        ) {
            Row {
                BitmapImage(bitmap = image, size = 80.dp)
                Column {
                    Text(
                        text = "00000111",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Left,
                        modifier = Modifier
                            .padding(0.dp, 4.dp)
                            .size(80.dp, 30.dp)
                            .wrapContentHeight(Alignment.CenterVertically),
                    )
                    Text(
                        text = "R$1999,10",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Left,
                        modifier = Modifier
                            .padding(0.dp, 2.dp)
                            .size(80.dp, 30.dp)
                            .wrapContentHeight(Alignment.CenterVertically),
                    )
                }
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
