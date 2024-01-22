package com.dnovaes.stockcontrol.common.ui.genericscreens.printer

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dnovaes.stockcontrol.R
import com.dnovaes.stockcontrol.common.ui.components.LoadingOverlay
import com.dnovaes.stockcontrol.common.ui.components.StockButton
import com.dnovaes.stockcontrol.common.ui.components.StockNegativeButton
import com.dnovaes.stockcontrol.common.utils.QRCodeManager
import com.dnovaes.stockcontrol.ui.icons.printerFilledIcon
import com.dnovaes.stockcontrol.ui.theme.AnnePrimary
import com.smarttoolfactory.screenshot.ScreenshotBox
import com.smarttoolfactory.screenshot.rememberScreenshotState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrintPreviewPage(
    viewModel: PrintPreviewVIewModel,
    sku: String,
    onNegativeButtonClick: () -> Unit,
) {
    val qRCodeImage = QRCodeManager.generateQRCode(sku, 100, 100)

    val snackbarHostState = remember { SnackbarHostState() }
    val rememberScope = rememberCoroutineScope()

    val uiState = viewModel.printState.value

    when {
        uiState.isProcessingPrint() -> LoadingOverlay(stringResource(R.string.print_loading_screen_label))
        (uiState.isDonePrint()) -> {
            uiState.error?.let { uiError ->
                val errorMessage = stringResource(id = uiError.errorCode.resId)
                rememberScope.launch {
                    snackbarHostState.showSnackbar(
                        message = errorMessage,
                        duration = SnackbarDuration.Short,
                    )
                }
            } ?: run {
                val message = stringResource(id = R.string.printer_label_printed)
                rememberScope.launch {
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Short,
                    )
                }
            }
            viewModel.snackBarShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { _ ->

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            Icon(
                imageVector = printerFilledIcon(),
                contentDescription = "Header icon",
                modifier = Modifier
                    .size(80.dp),
                tint = AnnePrimary,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.preview_print_label_title),
                color = AnnePrimary,
                fontSize = 28.sp,
                lineHeight = 36.sp,
                maxLines = 2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    vertical = 10.dp,
                    horizontal = 80.dp
                ),
            )
            Spacer(modifier = Modifier.height(60.dp))

            val screenshotState = rememberScreenshotState()

            Box(
                modifier = Modifier
                    .background(SolidColor(Color.White))
                    .border(
                        width = 12.dp,
                        color = Color(0xFFBFCAC2),
                        shape = RectangleShape
                    )
                    .width(168.dp)
                    .height(98.dp)
            ) {
                ScreenshotBox(screenshotState = screenshotState) {
                    PreviewPrintLabel(image = qRCodeImage)
                }

                SideEffect {
                    screenshotState.capture()
                }
            }

            Text(
                text = stringResource(id = R.string.empty),
                color = AnnePrimary,
                fontSize = 16.sp,
                maxLines = 2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(30.dp),
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {

                StockButton(
                    stringResource(R.string.print_bt_label),
                    Modifier.padding(vertical = 4.dp)
                ) {
                    screenshotState.bitmapState.value?.let {
                        viewModel.userRequestedToPrint(it)
                    }
                }

                StockNegativeButton(
                    stringResource(R.string.generic_success_page_negative_bt_label),
                    Modifier.padding(vertical = 4.dp)
                ) {
                    onNegativeButtonClick()
                }
            }
        }
    }

    uiState.error?.let { uiError ->
    }
}

@Composable
fun PreviewPrintLabel(image: Bitmap) {
    Row (
        modifier = Modifier.padding(8.dp)
    ) {
        Image(
            bitmap = image.asImageBitmap(),
            contentDescription = "QR code image",
            modifier = Modifier.size(80.dp)
        )
        Column {
            Text(
                text = "00000111",
                fontSize = 12.sp,
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .padding(0.dp, 4.dp)
                    .size(80.dp, 30.dp)
                    .wrapContentHeight(Alignment.CenterVertically),
                color = Color.Black
            )
            Text(
                text = "R$1999,10",
                fontSize = 12.sp,
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .padding(0.dp, 2.dp)
                    .size(80.dp, 30.dp)
                    .wrapContentHeight(Alignment.CenterVertically),
                color = Color.Black
            )
        }
    }
}
