package com.dnovaes.stockcontrol.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dnovaes.stockcontrol.R
import com.dnovaes.stockcontrol.common.ui.components.LoadingOverlay
import com.dnovaes.stockcontrol.common.ui.components.StockButton
import com.dnovaes.stockcontrol.features.landing.viewmodel.LandingViewModel

@Composable
fun LandingPage(
    viewModel: LandingViewModel,
    onClickAdd: () -> Unit,
    onClickManage: () -> Unit,
) {
    LaunchedEffect(key1 = true) {
        viewModel.loadCategories()
    }

    val currentState = viewModel.state.value
    when {
        currentState.isLoadingInitialData() -> {
            LoadingOverlay(stringResource(id = R.string.generic_loading_screen_label))
        }
        else -> {
            LandingMenuPage(onClickAdd, onClickManage)
        }
    }

}

@Composable
fun LandingMenuPage(
    onClickAdd: () -> Unit,
    onClickManage: () -> Unit,
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
                onClickAdd()
            }
            StockButton(
                stringResource(R.string.landing_page_manage_stock),
            ) {
                onClickManage()
            }
        }
    }
}