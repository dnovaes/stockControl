package com.dnovaes.stockcontrol.features.manageproduct.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dnovaes.stockcontrol.R
import com.dnovaes.stockcontrol.common.monitoring.log
import com.dnovaes.stockcontrol.common.ui.components.LoadingOverlay
import com.dnovaes.stockcontrol.common.ui.components.StockOutlineTextField
import com.dnovaes.stockcontrol.features.manageproduct.viewmodel.ManageViewModel
import com.dnovaes.stockcontrol.ui.theme.AnnePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageProductPage(
    viewModel: ManageViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                        }
                    )
                }
            )
        }
    ) { paddingValues ->

        LaunchedEffect(key1 = true) {
            viewModel.loadInitialData()
        }
        ManageSearchPage(paddingValues, viewModel)
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
        currentState.isDoneLoadInitialData() -> {
            //Populate Table :TODO
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        StockOutlineTextField(
            labelText = "Procurar Produto",
            currentValue = "",
            enabled = true,
            onValueChange = {
                viewModel.searchProductByName(it)
            },
        )

    }
}
