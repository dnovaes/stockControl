package com.dnovaes.stockcontrol.common

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dnovaes.stockcontrol.R
import com.dnovaes.stockcontrol.common.extensions.navigateSingleTopTo
import com.dnovaes.stockcontrol.common.ui.genericscreens.FullScreenAlert
import com.dnovaes.stockcontrol.common.ui.genericscreens.printer.PrintPreviewPage
import com.dnovaes.stockcontrol.common.ui.genericscreens.printer.PrintPreviewVIewModel
import com.dnovaes.stockcontrol.features.addproduct.ui.AddProductPage
import com.dnovaes.stockcontrol.features.updateproduct.ui.UpdateProductPage
import com.dnovaes.stockcontrol.ui.pages.LandingPage
import com.dnovaes.stockcontrol.utilities.StockBluetoothManager

@Composable
fun StockNavHost(
    context: Context,
    navHostController: NavHostController,
    bluetoothManager: StockBluetoothManager
) {
    NavHost(
        navController = navHostController,
        startDestination = "LandingPage",
    ) {
        composable(route = "LandingPage") {
            LandingPage(
                navHostController = navHostController
            )
        }
        composable(route = "AddProductPage") {
            AddProductPage(
                context = context,
                onBackPressed =  {
                    navHostController.popBackStack()
                },
                onFinishRegistration = { product ->
                    navHostController.navigateSingleTopTo(
                        "SuccessfulAddRegistrationPage/${product.id}"
                    )
                }
            )
        }
        composable(route = "SuccessfulAddRegistrationPage/{productId}") { backstackEntry ->
            val productId = backstackEntry.arguments?.getString("productId")
            FullScreenAlert(
                headerIcon = Icons.Filled.ThumbUp,
                title = R.string.add_success_page_title,
                subtitle = null,
                positiveButtonLabel = R.string.add_success_page_positive_bt_label,
                negativeButtonLabel = R.string.generic_success_page_negative_bt_label,
                onPositiveButtonClick = {
                    navHostController.navigateSingleTopTo("UpdateProductPage/$productId")
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

        composable(route = "UpdateProductPage/{productId}") {backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: "-1"
            UpdateProductPage(
                productId = productId,
                onBackPressed =  {
                    navHostController.popBackStack(route = "LandingPage", inclusive = false)
                },
                onFinishRegistration = { sku, price ->
                    navHostController.navigateSingleTopTo("SuccessfulUpdateProductPage/$sku/${price}")
                }
            )
        }

        composable(route = "SuccessfulUpdateProductPage/{sku}/{price}") {
            val sku = it.arguments?.getString("sku") ?: "0"
            val price = it.arguments?.getString("price") ?: "0"
            FullScreenAlert(
                headerIcon = Icons.Filled.ThumbUp,
                title = R.string.update_success_page_title,
                subtitle = null,
                positiveButtonLabel = R.string.update_success_page_positive_bt_label,
                negativeButtonLabel = R.string.generic_success_page_negative_bt_label,
                onPositiveButtonClick = {
                    navHostController.navigateSingleTopTo("PrintPreviewPage/$sku/$price")
                },
                onNegativeButtonClick = {
                    navHostController.popBackStack()
                }
            )
        }

        composable(route = "PrintPreviewPage/{sku}/{price}") {
            val sku = it.arguments?.getString("sku") ?: "0"
            val price = it.arguments?.getString("price") ?: "0,00"
            PrintPreviewPage(
                viewModel = PrintPreviewVIewModel(bluetoothManager),
                sku = sku,
                price = price,
                onNegativeButtonClick = {
                    navHostController.popBackStack()
                }
            )
        }
    }
}
