package com.dnovaes.stockcontrol.features.updateproduct.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.dnovaes.stockcontrol.AddStockProductMutation
import com.dnovaes.stockcontrol.common.models.ErrorCode
import com.dnovaes.stockcontrol.common.models.ErrorCodeInterface
import com.dnovaes.stockcontrol.common.models.State
import com.dnovaes.stockcontrol.common.models.UIErrorInterface
import com.dnovaes.stockcontrol.common.models.business.StockProduct
import com.dnovaes.stockcontrol.common.monitoring.log
import com.dnovaes.stockcontrol.common.utils.SessionManager
import com.dnovaes.stockcontrol.features.addproduct.models.AddUIError
import com.dnovaes.stockcontrol.features.updateproduct.models.UpdateProcess
import com.dnovaes.stockcontrol.features.updateproduct.models.UpdateUIModel
import com.dnovaes.stockcontrol.features.updateproduct.models.UpdateUIObservable
import com.dnovaes.stockcontrol.type.NewStockProduct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateViewModel @Inject constructor(
    private val apolloClient: ApolloClient
): ViewModel() {

    private val initialObservable = UpdateUIObservable(
        state = State.IDLE,
        process = UpdateProcess.LOAD_INITIAL_DATA,
        data = UpdateUIModel(),
    )

    private var _updateState = initialObservable
    val updateState: MutableState<UpdateUIObservable> = mutableStateOf(_updateState)

    fun resetState() {
        _updateState = initialObservable
        updateState.value = _updateState
    }

    fun createStockProduct(
        newStockProduct: NewStockProduct,
    ) {
        _updateState = _updateState.asUpdatingProduct()
        updateState.value = _updateState

        viewModelScope.launch(Dispatchers.IO) {
            viewModelScope.launch(Dispatchers.IO) {
                apolloClient.mutation(AddStockProductMutation(newStockProduct))
                    .toFlow()
                    .catch {
                        log("localizedMessage: ${it.localizedMessage}, cause: ${it.cause}")
                        val error = AddUIError(
                            errorCode = ErrorCode.UNKNOWN_EXCEPTION,
                            additionalParams = emptyMap()
                        )
                        _updateState = _updateState.withError(error)
                    }
                    .collectLatest {
                        processAddStockProductResponse(it)
                    }
            }
        }
    }

    private fun processAddStockProductResponse(response: ApolloResponse<AddStockProductMutation.Data>) {
        val logTag = "addStockProduct"
        response.data?.let {
            it.createStockProduct.apply {
                log("$logTag) success response: $this")
                val newStockProduct = StockProduct(
                    productId = product.id,
                    productName = product.name,
                    storeId = store.id,
                    styleColor = styleColor,
                    size = size,
                    sku = sku,
                    price = price,
                    quantity = quantity,
                )
                SessionManager.saveStockProduct(newStockProduct)
                val newModel = _updateState.data.copy(
                    lastAddedStockProduct = newStockProduct
                )
                _updateState = _updateState
                    .withData(newModel)
                    .asDoneUpdateProduct()
                updateState.value = _updateState
            }
        } ?: response.errors?.let {
            log("$logTag) fail response: $it")
            val error = AddUIError(
                errorCode = ErrorCode.UNKNOWN_EXCEPTION,
                additionalParams = emptyMap()
            )
            _updateState = _updateState
                .withError(error)
                .asDoneUpdateProduct()
            updateState.value = _updateState
        }
    }

    fun loadProductInfo(productId: String) {
        _updateState = _updateState.asLoadingInitialData()
        updateState.value = _updateState

        viewModelScope.launch {
            SessionManager.loadLocalProductInfo(productId)?.let { product ->
                val newModel = UpdateUIModel(productInfo = product)
                _updateState = _updateState.asDoneLoadInitialData()
                    .withData(newModel)
                updateState.value = _updateState
            } ?: run {
                _updateState = _updateState
                    .asDoneLoadInitialData()
                    .withError(object : UIErrorInterface {
                        override val errorCode: ErrorCodeInterface = ErrorCode.INVALID_PRODUCT_MODEL
                        override val additionalParams: Map<String, String> = mapOf("productId" to productId)
                    })
                updateState.value = _updateState
            }
        }
    }
}
