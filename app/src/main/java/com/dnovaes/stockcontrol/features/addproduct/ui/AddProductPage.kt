package com.dnovaes.stockcontrol.features.addproduct.ui

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dnovaes.stockcontrol.R
import com.dnovaes.stockcontrol.common.models.business.Product
import com.dnovaes.stockcontrol.common.models.business.ProductCategory
import com.dnovaes.stockcontrol.common.monitoring.log
import com.dnovaes.stockcontrol.common.ui.components.LoadingOverlay
import com.dnovaes.stockcontrol.common.ui.components.StockButton
import com.dnovaes.stockcontrol.common.ui.components.StockNegativeButton
import com.dnovaes.stockcontrol.common.ui.components.StockOutlineTextField
import com.dnovaes.stockcontrol.ui.icons.uploadIcon
import com.dnovaes.stockcontrol.ui.theme.AnneBackground
import com.dnovaes.stockcontrol.ui.theme.AnnePrimary
import com.dnovaes.stockcontrol.ui.theme.defaultPadding
import com.dnovaes.stockcontrol.ui.theme.photoButtonSize
import com.dnovaes.stockcontrol.features.addproduct.viewmodel.AddViewModel
import com.dnovaes.stockcontrol.type.NewProduct
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductPage(
    context: Context,
    viewModel: AddViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onFinishRegistration: (Product) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val rememberScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.add_product_screen_title),
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
        val currentState = viewModel.addState.value

        when {
            currentState.isRegisteringProduct() -> {
                LoadingOverlay(stringResource(R.string.add_loading_screen_label))
            }
            currentState.isDoneProductRegistration() -> {
                currentState.error?.let {
                    val errorMessage = stringResource(id = it.errorCode.resId)
                    rememberScope.launch {
                        snackbarHostState.showSnackbar(
                            message = errorMessage,
                            duration = SnackbarDuration.Short,
                        )
                    }
                } ?: run {
                    currentState.data.lastAddedProduct?.let { product ->
                        onFinishRegistration(product)
                    } ?: run {
                        val errorMessage = stringResource(id = R.string.invalid_product_model)
                        rememberScope.launch {
                            snackbarHostState.showSnackbar(
                                message = errorMessage,
                                duration = SnackbarDuration.Short,
                            )
                        }
                    }
                }
            }
        }

        AddInitialPage(context, paddingValues, viewModel, onBackPressed = onBackPressed)
    }
}

@Composable
fun AddInitialPage(
    context: Context,
    paddingValues: PaddingValues,
    viewModel: AddViewModel,
    onBackPressed: () -> Unit
) {
    val currentState = viewModel.addState.value

    val fileName = "add_product_temp_image_${System.currentTimeMillis()}.jpg"
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }

    // Insert an empty image file into the MediaStore
    val imageUri =
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    val cameraResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = loadImageBitmap(imageUri, context.contentResolver)
                viewModel.finishImageCapturing(imageBitmap)
            } else {
                log("User canceled capture of image from camera")
            }
        }

    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
        putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraResultLauncher.launch(takePictureIntent)
        } else {
            log("Camera permission not granted.")
        }
    }

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
            onClick = {
                permissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        ) {
            currentState.data.registerImage ?.let {
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
        var selectedCategory by remember {
            mutableStateOf(
                currentState.data.categories.firstOrNull() ?: ProductCategory.EMPTY_CATEGORY
            )
        }

        StockDropdownField(
            selectedCategory = selectedCategory.name,
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
                    currentState.data.categories.forEachIndexed { _, productCategory ->
                        val menuItemText = productCategory.name
                        DropdownMenuItem(
                            text = { Text(menuItemText) },
                            onClick = {
                                expandedDropdown = false
                                selectedCategory = productCategory
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
                stringResource(R.string.add_register_label_bt),
                Modifier.padding(vertical = 4.dp)
            ) {
                val newProduct = NewProduct(
                    name = nameState.value,
                    image = "",
                    brand = brandNameState.value,
                    supplier = supplierNameState.value,
                    categoryId = selectedCategory.id,
                    companyId = "1"
                )
                viewModel.registerProduct(newProduct)
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

private fun loadImageBitmap(
    imageUri: Uri?,
    contentResolver: ContentResolver
): Bitmap? {
    return try {
        imageUri?.let { uri ->
            val inputStream = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        }
    } catch (e: Exception) {
        log("Failed to load image bitmap")
        null
    }
}
