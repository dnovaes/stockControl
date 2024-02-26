package com.dnovaes.stockcontrol.features.manageproduct.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dnovaes.stockcontrol.R
import com.dnovaes.stockcontrol.common.models.business.StockProduct
import com.dnovaes.stockcontrol.common.monitoring.log
import com.dnovaes.stockcontrol.common.ui.components.LoadingOverlay
import com.dnovaes.stockcontrol.common.ui.components.QRCodeReaderScreen
import com.dnovaes.stockcontrol.features.manageproduct.viewmodel.ManageViewModel
import com.dnovaes.stockcontrol.ui.theme.AnneBackground
import com.dnovaes.stockcontrol.ui.theme.AnnePrimary
import com.dnovaes.stockcontrol.ui.theme.AnneTableHeader
import com.dnovaes.stockcontrol.ui.theme.AnneViewIconBackground
import com.dnovaes.stockcontrol.ui.theme.defaultPadding
import kotlinx.coroutines.launch

@Composable
fun ManageProductPage(
    viewModel: ManageViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val rememberScope = rememberCoroutineScope()

    val currentState = viewModel.state.value
    when  {
        currentState.isRequestingCamera() -> {
            QRCodeReaderScreen(
                onFinishCapture = {
                    viewModel.finishCapturing()
                }
            )
        }
        else -> {
            currentState.error?.let {
                val errorMessage = stringResource(id = currentState.error.errorCode.resId)
                rememberScope.launch {
                    snackBarHostState.showSnackbar(
                        message = errorMessage,
                        duration = SnackbarDuration.Short,
                    )
                }
                viewModel.snackBarShown()
            }
            ManageProductScaffoldPage(
                snackBarHostState,
                viewModel,
                onBackPressed
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageProductScaffoldPage(
    snackBarHostState: SnackbarHostState,
    viewModel: ManageViewModel,
    onBackPressed: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val anneIcon = ImageVector.vectorResource(id = R.drawable.anne_icon)
                        Icon(
                            imageVector = anneIcon,
                            contentDescription = "Anne Icon",
                            tint = AnnePrimary,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        colors = IconButtonDefaults.iconButtonColors(contentColor = AnnePrimary),
                        onClick = {
                            viewModel.resetState()
                            onBackPressed()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = AnnePrimary,
                        )
                    }
                },
                actions = {
                    val barCodeIcon = ImageVector.vectorResource(id = R.drawable.barcode_icon)
                    Icon(
                        imageVector = barCodeIcon,
                        contentDescription = "Action Icon",
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 0.dp)
                            .clickable {
                                log("barcode clicked")
                                viewModel.requestCamera()
                            }
                    )
                }
            )
        }
    ) { paddingValues ->

        LaunchedEffect(key1 = true) {
            viewModel.loadInitialData()
        }
        ManageSearchPage(
            paddingValues,
            viewModel,
        )
    }
}

@Composable
fun ManageSearchPage(
    paddingValues: PaddingValues,
    viewModel: ManageViewModel,
) {
    val currentState = viewModel.state.value

    when {
        currentState.isLoadingInitialData() -> {
            LoadingOverlay(stringResource(R.string.manage_loading_screen_label))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = defaultPadding.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        OutlinedTextField(
            value = "",
            label = { Text(stringResource(R.string.manage_search_title_field)) },
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            trailingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            },
            onValueChange = {
                viewModel.searchProductByName(it)
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )

        val products = currentState.data.products
        Table(products)
    }
}

@Composable
fun Table(products: List<StockProduct>) {
    if (products.isEmpty()) {
        return
    }

    Box(modifier = Modifier.padding(vertical = 8.dp)) {
        TableHeader(
            rowContent = ManageRowData.header,
        )
    }

    TableScrollableBody(products)
}

@Composable
fun TableScrollableBody(products: List<StockProduct>) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        products.forEachIndexed { _, stockProduct ->
            TableRow(
                rowContent = ManageRowData(
                    itemTitle = stockProduct.productName,
                    itemQuantity = stockProduct.quantity.toString(),
                    itemPrice = stockProduct.price
                )
            )
        }
    }
}

@Composable
fun TableHeader(
    rowContent: ManageRowData,
) {
    val headerFontSize = 18.sp
    Row(
        modifier = Modifier
            .background(AnneTableHeader)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "",
            color = AnnePrimary,
            fontSize = headerFontSize,
            modifier = Modifier
                .weight(0.5f),
            textAlign = TextAlign.Left
        )
        Text(
            text = rowContent.itemTitle,
            color = AnnePrimary,
            fontSize = headerFontSize,
            modifier = Modifier
                .weight(1.2f)
                .padding(horizontal = 4.dp),
            textAlign = TextAlign.Left
        )
        Text(
            text = rowContent.itemQuantity,
            color = AnnePrimary,
            fontSize = headerFontSize,
            modifier = Modifier
                .weight(0.8f),
            textAlign = TextAlign.Center
        )
        Text(
            text = rowContent.itemPrice,
            color = AnnePrimary,
            fontSize = headerFontSize,
            modifier = Modifier
                .weight(0.8f),
            textAlign = TextAlign.Center
        )
        Text(
            text = "",
            color = AnnePrimary,
            fontSize = headerFontSize,
            modifier = Modifier
                .weight(0.5f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TableRow(rowContent: ManageRowData) {
    val rowTextSize = 18.sp
    Row(
        modifier = Modifier
            .background(AnneBackground)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            ImageBitmap.imageResource(id = R.mipmap.thumbnail_placeholder),
            contentDescription = "Product Image",
            modifier = Modifier
                .size(width = 50.dp, height = 50.dp)
        )
        Text(
            text = rowContent.itemTitle,
            color = AnnePrimary,
            fontSize = rowTextSize,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1.2f)
                .padding(horizontal = 4.dp),
            textAlign = TextAlign.Left,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = rowContent.itemQuantity,
            color = AnnePrimary,
            fontSize = rowTextSize,
            modifier = Modifier
                .weight(0.8f)
                .fillMaxHeight(),
            textAlign = TextAlign.Center
        )
        Text(
            text = rowContent.itemPrice,
            color = AnnePrimary,
            fontSize = rowTextSize,
            modifier = Modifier
                .weight(0.8f)
                .fillMaxHeight(),
            textAlign = TextAlign.Center
        )
        Card(
            modifier = Modifier
                .size(50.dp, 50.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = AnneViewIconBackground
            )
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.eye),
                contentDescription = "View Icon",
                tint = AnnePrimary,
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .background(AnneTableHeader)
            .padding(1.dp),
    ) {

    }
}

