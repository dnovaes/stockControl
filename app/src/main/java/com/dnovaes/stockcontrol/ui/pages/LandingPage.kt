package com.dnovaes.stockcontrol.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.dnovaes.stockcontrol.R
import com.dnovaes.stockcontrol.common.ui.components.LoadingOverlay
import com.dnovaes.stockcontrol.common.ui.components.StockButton
import com.dnovaes.stockcontrol.features.landing.viewmodel.LandingViewModel
import kotlinx.coroutines.launch

@Composable
fun LandingPage(
    viewModel: LandingViewModel = hiltViewModel(),
    navHostController: NavHostController
) {
    LaunchedEffect(key1 = true) {
        viewModel.loadCategories()
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val rememberScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { _ ->

        val currentState = viewModel.state.value

        when {
            currentState.isLoadingInitialData() -> {
                LoadingOverlay(stringResource(id = R.string.generic_loading_screen_label))
            }
            else -> {
                if (currentState.error != null) {
                    val errorMessage = stringResource(id = currentState.error.errorCode.resId)
                    run {
                        rememberScope.launch {
                            snackbarHostState.showSnackbar(
                                message = errorMessage,
                                duration = SnackbarDuration.Short,
                            )
                        }
                        viewModel.snackBarShown()
                    }
                }
                LandingMenuPage(navHostController)
            }
        }
    }
}

@Composable
fun LandingMenuPage(
    navHostController: NavHostController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 72.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logomenu), // Replace with your image resource
            contentDescription = "Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(0.dp, 6.dp, 0.dp, 6.dp),
            alignment = Alignment.TopCenter,
        )

        Column(
            modifier = Modifier.align(Alignment.Center)
        ) {
            StockButton(
                textContent = stringResource(R.string.landing_page_menu_register_products),
            ) {
                navHostController.navigate("AddProductPage")
            }
            StockButton(
                stringResource(R.string.landing_page_manage_stock),
            ) {
                //onClickManage()
            }
        }
    }
}