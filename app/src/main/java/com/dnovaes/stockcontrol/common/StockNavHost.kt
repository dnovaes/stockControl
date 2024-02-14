package com.dnovaes.stockcontrol.common

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.apollographql.apollo3.ApolloClient
import com.dnovaes.stockcontrol.R
import com.dnovaes.stockcontrol.common.extensions.navigateSingleTopTo
import com.dnovaes.stockcontrol.common.ui.genericscreens.FullScreenAlert
import com.dnovaes.stockcontrol.common.ui.genericscreens.printer.PrintPreviewPage
import com.dnovaes.stockcontrol.common.ui.genericscreens.printer.PrintPreviewVIewModel
import com.dnovaes.stockcontrol.features.addproduct.ui.AddProductPage
import com.dnovaes.stockcontrol.features.addproduct.viewmodel.AddViewModel
import com.dnovaes.stockcontrol.features.landing.viewmodel.LandingViewModel
import com.dnovaes.stockcontrol.features.updateproduct.ui.UpdateProductPage
import com.dnovaes.stockcontrol.features.updateproduct.viewmodel.UpdateViewModel
import com.dnovaes.stockcontrol.ui.pages.LandingPage
import com.dnovaes.stockcontrol.utilities.StockBluetoothManager

@Composable
fun StockNavHost(
    context: Context,
    serviceClient: ApolloClient,
    navHostController: NavHostController,
    bluetoothManager: StockBluetoothManager
) {
    NavHost(
        navController = navHostController,
        startDestination = "LandingPage",
    ) {
        composable(route = "LandingPage") {
            LandingPage(
                viewModel = LandingViewModel(serviceClient),
                onClickAdd = {
                    navHostController.navigateSingleTopTo("AddProductPage")
                },
                onClickManage = { }
            )
        }
        composable(route = "AddProductPage") {
            AddProductPage(
                context = context,
                viewModel = AddViewModel(serviceClient),
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
                negativeButtonLabel = R.string.generic_success_page_negative_bt_label,
                onPositiveButtonClick = {
                    navHostController.navigateSingleTopTo("UpdateProductPage")
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

        composable(route = "UpdateProductPage") {
            UpdateProductPage(
                context = context,
                viewModel = UpdateViewModel(),
                onBackPressed =  {
                    navHostController.popBackStack(route = "LandingPage", inclusive = false)
                },
                onFinishRegistration = {
                    navHostController.navigateSingleTopTo("SuccessfulUpdateProductPage")
                }
            )
        }

        composable(route = "SuccessfulUpdateProductPage") {
            FullScreenAlert(
                headerIcon = Icons.Filled.ThumbUp,
                title = R.string.update_success_page_title,
                subtitle = null,
                positiveButtonLabel = R.string.update_success_page_positive_bt_label,
                negativeButtonLabel = R.string.generic_success_page_negative_bt_label,
                onPositiveButtonClick = {
                    navHostController.navigateSingleTopTo("PrintPreviewPage")
                },
                onNegativeButtonClick = {
                    navHostController.popBackStack()
                }
            )
        }

        composable(route = "PrintPreviewPage") {
            PrintPreviewPage(
                viewModel = PrintPreviewVIewModel(bluetoothManager),
                sku = "00000111",
                onNegativeButtonClick = {
                    navHostController.popBackStack()
                }
            )
        }
    }
}
