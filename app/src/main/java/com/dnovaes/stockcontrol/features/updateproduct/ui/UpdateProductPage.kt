package com.dnovaes.stockcontrol.features.updateproduct.ui

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dnovaes.stockcontrol.R
import com.dnovaes.stockcontrol.common.ui.components.LoadingOverlay
import com.dnovaes.stockcontrol.common.ui.components.StockButton
import com.dnovaes.stockcontrol.common.ui.components.StockNegativeButton
import com.dnovaes.stockcontrol.common.ui.components.StockOutlineTextField
import com.dnovaes.stockcontrol.ui.icons.uploadIcon
import com.dnovaes.stockcontrol.ui.theme.AnneBackground
import com.dnovaes.stockcontrol.ui.theme.AnnePrimary
import com.dnovaes.stockcontrol.ui.theme.defaultPadding
import com.dnovaes.stockcontrol.ui.theme.photoButtonSize
import com.dnovaes.stockcontrol.features.updateproduct.viewmodel.UpdateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProductPage(
    context: Context,
    viewModel: UpdateViewModel,
    onBackPressed: () -> Unit,
    onFinishRegistration: () -> Unit
) {
    Scaffold(
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
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        val currentState = viewModel.updateState.value

        when {
            currentState.isUpdatingProduct() -> {
                LoadingOverlay(stringResource(R.string.update_loading_screen_label))
            }
            currentState.isDoneUpdateProduct() -> {
                onFinishRegistration()
            }
        }

        UpdateFieldsPage(paddingValues, viewModel, onBackPressed = onBackPressed)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateFieldsPage(
    paddingValues: PaddingValues,
    viewModel: UpdateViewModel,
    onBackPressed: () -> Unit
) {
    val model = viewModel.updateState.value.data

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

        val nameState = remember { mutableStateOf("") }
        StockOutlineTextField(
            labelText = "Nome",
            currentValue = nameState.value,
            onValueChange = {
                nameState.value = it
            },
        )

        var expandedDropdown by remember { mutableStateOf(false) }
        var selectedCategory by remember { mutableStateOf("") }

        StockDropdownField(
            selectedCategory = selectedCategory,
            onClick = {
                expandedDropdown = !expandedDropdown
            }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 0.dp)
        ) {
            DropdownMenu(
                expanded = expandedDropdown,
                onDismissRequest = { },
                content = {
                    for (i in 0..10) {
                        val menuItemText = "Categoria$i"
                        DropdownMenuItem(
                            text = { Text(menuItemText) },
                            onClick = {
                                expandedDropdown = false
                                selectedCategory = menuItemText
                            }
                        )
                    }
                }
            )
        }

        val brandNameState = remember { mutableStateOf("") }
        StockOutlineTextField(
            labelText = stringResource(R.string.product_brand_field),
            currentValue = brandNameState.value,
            onValueChange = {
                brandNameState.value = it
            },
        )

        val supplierNameState = remember { mutableStateOf("") }
        StockOutlineTextField(
            labelText = stringResource(R.string.product_provider_field),
            currentValue = supplierNameState.value,
            onValueChange = {
                supplierNameState.value = it
            },
        )

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
                viewModel.updateProduct()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDropdownField(
    selectedCategory: String,
    onClick: (() -> Unit),
) {
    OutlinedTextField(
        value = selectedCategory,
        onValueChange = {},
        label = { Text(stringResource(id = R.string.add_product_category_field)) },
        modifier = Modifier
            .fillMaxWidth()
            .focusable(false)
            .padding(horizontal = defaultPadding.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        readOnly = true,
        enabled = false,
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "",
            )
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            disabledBorderColor = AnnePrimary,
            disabledLabelColor = AnnePrimary,
            disabledPlaceholderColor = AnnePrimary,
            disabledTextColor = AnnePrimary,
            disabledTrailingIconColor = AnnePrimary
        )
    )
}
