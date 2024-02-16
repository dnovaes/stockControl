package com.dnovaes.stockcontrol.features.updateproduct.ui

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dnovaes.stockcontrol.R
import com.dnovaes.stockcontrol.common.extensions.formatCurrency
import com.dnovaes.stockcontrol.common.models.business.ProductSize.Companion.UNIQUE_SIZE
import com.dnovaes.stockcontrol.common.monitoring.log
import com.dnovaes.stockcontrol.common.ui.components.LoadingOverlay
import com.dnovaes.stockcontrol.common.ui.components.StockButton
import com.dnovaes.stockcontrol.common.ui.components.StockDropdownField
import com.dnovaes.stockcontrol.common.ui.components.StockNegativeButton
import com.dnovaes.stockcontrol.common.ui.components.StockOutlineTextField
import com.dnovaes.stockcontrol.common.utils.SessionManager
import com.dnovaes.stockcontrol.ui.icons.uploadIcon
import com.dnovaes.stockcontrol.ui.theme.AnneBackground
import com.dnovaes.stockcontrol.ui.theme.AnnePrimary
import com.dnovaes.stockcontrol.ui.theme.defaultPadding
import com.dnovaes.stockcontrol.ui.theme.photoButtonSize
import com.dnovaes.stockcontrol.features.updateproduct.viewmodel.UpdateViewModel
import com.dnovaes.stockcontrol.type.NewStockProduct

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProductPage(
    productId: String,
    viewModel: UpdateViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onFinishRegistration: (String, String) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.update_product_screen_title),
                        color = AnnePrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        colors = IconButtonDefaults.iconButtonColors(contentColor = AnnePrimary),
                        onClick = {
                            viewModel.resetState()
                            onBackPressed()
                        }
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->

        LaunchedEffect(key1 = true) {
            viewModel.loadProductInfo(productId)
        }
        UpdateFieldsPage(
            paddingValues,
            viewModel,
            onBackPressed = onBackPressed,
            onFinishRegistration = onFinishRegistration
        )
    }
}

@Composable
fun UpdateFieldsPage(
    paddingValues: PaddingValues,
    viewModel: UpdateViewModel,
    onBackPressed: () -> Unit,
    onFinishRegistration: (String, String) -> Unit
) {
    val currentState = viewModel.updateState.value

    when {
        currentState.isUpdatingProduct() -> {
            LoadingOverlay(stringResource(R.string.update_loading_screen_label))
        }
        currentState.isDoneUpdateProduct() -> {
            currentState.data.lastAddedStockProduct?.let {
                onFinishRegistration(it.sku, it.price)
            }
        }
    }

    val model = currentState.data
    val product = model.productInfo ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Card(
            modifier = Modifier
                .size(photoButtonSize.dp)
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                contentColor = AnnePrimary,
                containerColor = AnneBackground
            ),
            border = BorderStroke(1.dp, SolidColor(AnnePrimary)),
            shape = RoundedCornerShape(10.dp),
        ) {
            model.registerImage ?.let {
                FilledPhotoButton(it)
            } ?: run {
                UnfilledPhotoButton()
            }
        }

        val nameState = remember { mutableStateOf(product.name) }
        StockOutlineTextField(
            labelText = "Nome",
            currentValue = nameState.value,
            enabled = false,
            onValueChange = {
                nameState.value = it
            },
        )

        val categoryName = SessionManager.loadProductCategories().firstOrNull { it.id == product.categoryId }?.name
        StockDropdownField(
            label = R.string.add_product_category_field,
            value = categoryName ?: "",
            modifier = Modifier
                .padding(
                    horizontal = defaultPadding.dp,
                    vertical = 0.dp,
                )
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(),
            readOnly = true,
            enabled = false,
            onClick =  null
        )

        val brandNameState = remember { mutableStateOf(product.brand) }
        StockOutlineTextField(
            labelText = stringResource(R.string.product_brand_field),
            currentValue = brandNameState.value,
            enabled = false,
            onValueChange = {
                brandNameState.value = it
            },
        )

        val supplierNameState = remember { mutableStateOf(product.supplier) }
        StockOutlineTextField(
            labelText = stringResource(R.string.product_provider_field),
            currentValue = supplierNameState.value,
            enabled = false,
            onValueChange = {
                supplierNameState.value = it
            },
        )

        val priceSellNameState = remember { mutableStateOf(TextFieldValue("0,00")) }
        val stockQuantityState = remember { mutableIntStateOf(1) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            StockOutlineTextField(
                modifier = Modifier
                    .padding(
                        start = defaultPadding.dp,
                        end = 4.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    )
                    .fillMaxWidth(0.5f),
                labelText = stringResource(R.string.update_product_sell_price),
                currentValue = priceSellNameState.value,
                leadingIcon = {
                    IconButton(
                        onClick = {
                            //nothing }
                        }
                    ) {
                        Text(text = "R$")
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number,
                    autoCorrect = false
                ),
                onValueChange = { newPrice ->
                    val prevPrice = priceSellNameState.value
                    if (prevPrice.text.length > newPrice.text.length) {
                        //user tried to delete a character
                        val newValue = "0,00"
                        priceSellNameState.value = TextFieldValue(
                            text = newValue,
                            selection = TextRange(newValue.length)
                        )
                    } else {
                        //user added a character
                        priceSellNameState.value = newPrice.copy(
                            text = newPrice.text.formatCurrency(),
                            selection = TextRange(newPrice.text.length)
                        )
                    }
                },
            )

            OutlinedTextField(
                modifier = Modifier
                    .padding(
                        start = 4.dp,
                        end = defaultPadding.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    )
                    .fillMaxWidth(1f),
                label = {
                    Text(
                        text = "Estoque",
                        textAlign = TextAlign.Center,
                    )
                },
                value = stockQuantityState.intValue.toString(),
                textStyle = TextStyle(textAlign = TextAlign.Center),
                leadingIcon = {
                    IconButton(
                        onClick = {
                            log("plus icon clicked")
                            stockQuantityState.intValue++
                        }
                    ) {
                        Text(text = "+")
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            log("minus icon clicked")
                            if (stockQuantityState.intValue > 1) {
                                stockQuantityState.intValue--
                            }
                        }
                    ) {
                        Text(text = "-")
                    }
                },
                readOnly = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                onValueChange = {
                    stockQuantityState.intValue = it.toInt()
                },
            )
        }

        var expandedDropdown by remember { mutableStateOf(false) }
        var selectedSize by remember {
            mutableStateOf(currentState.data.sizes.firstOrNull() ?: UNIQUE_SIZE)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            StockDropdownField(
                label = R.string.product_size_label,
                value = selectedSize.code,
                modifier = Modifier
                    .padding(
                        horizontal = defaultPadding.dp,
                        vertical = 4.dp,
                    )
                    .fillMaxWidth(.5f),
                readOnly = true,
                enabled = true,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Black
                ),
                onClick = {
                    expandedDropdown = !expandedDropdown
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(.5f)
                    .padding(horizontal = 16.dp, vertical = 0.dp)
            ) {
                DropdownMenu(
                    expanded = expandedDropdown,
                    onDismissRequest = { },
                    content = {
                        currentState.data.sizes.forEachIndexed { _, size ->
                            DropdownMenuItem(
                                text = { Text(size.code) },
                                onClick = {
                                    expandedDropdown = false
                                    selectedSize = size
                                }
                            )
                        }
                    }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            StockButton(
                stringResource(R.string.update_product_positive_label_bt),
                Modifier.padding(vertical = 4.dp)
            ) {
                val newStockProduct = NewStockProduct(
                    price = priceSellNameState.value.text,
                    quantity = stockQuantityState.intValue,
                    size = selectedSize.id,
                    productId = product.id,
                    storeId = SessionManager.storeId
                )
                viewModel.createStockProduct(newStockProduct)
            }

            StockNegativeButton(
                stringResource(R.string.add_cancel_label_bt),
                Modifier.padding(vertical = 4.dp)
            ) {
                viewModel.resetState()
                onBackPressed()
            }
        }
    }
}

@Composable
fun UnfilledPhotoButton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(6.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = uploadIcon(),
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
        )
        Text(
            text = stringResource(R.string.send_photo),
            modifier = Modifier.padding(vertical = 4.dp),
        )
    }
}

@Composable
fun FilledPhotoButton(image: Bitmap) {
    Image(
        modifier = Modifier
            .fillMaxSize()
            .background(SolidColor(AnnePrimary)),
        bitmap = image.asImageBitmap(),
        contentDescription = null,
        contentScale = ContentScale.FillBounds
    )
}
