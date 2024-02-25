package com.dnovaes.stockcontrol.features.manageproduct.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.dnovaes.stockcontrol.GetAllStockProductQuery
import com.dnovaes.stockcontrol.common.data.requestSafely
import com.dnovaes.stockcontrol.common.models.ErrorCode
import com.dnovaes.stockcontrol.common.models.ErrorCodeInterface
import com.dnovaes.stockcontrol.common.models.State
import com.dnovaes.stockcontrol.common.models.UIErrorInterface
import com.dnovaes.stockcontrol.common.models.business.ProductCategory
import com.dnovaes.stockcontrol.common.models.business.StockProduct
import com.dnovaes.stockcontrol.common.monitoring.log
import com.dnovaes.stockcontrol.common.utils.SessionManager
import com.dnovaes.stockcontrol.features.addproduct.models.AddUIError
import com.dnovaes.stockcontrol.features.manageproduct.models.ManageProcess
import com.dnovaes.stockcontrol.features.manageproduct.models.ManageUIModel
import com.dnovaes.stockcontrol.features.manageproduct.models.ManageUIObservable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageViewModel @Inject constructor(
    private val apolloClient: ApolloClient
): ViewModel() {

    private val initialObservable = ManageUIObservable(
        state = State.IDLE,
        process = ManageProcess.LOAD_INITIAL_DATA,
        data = ManageUIModel(),
    )

    private var _state = initialObservable
    val state: MutableState<ManageUIObservable> = mutableStateOf(_state)

    fun resetState() {
        _state = initialObservable
        state.value = _state
    }

    fun searchProductByName(input: String) {
        //do Nothing
    }

    fun loadInitialData() {
        _state = _state.asLoadingInitialData()
        state.value = _state

        viewModelScope.launch {
            val logTag = "loadInitialData"
            delay(1000)

            requestSafely(
                logTag = logTag,
                block = {
                    val flow = apolloClient.query(GetAllStockProductQuery()).toFlow()
                    flow.catch {
                        log("$logTag) localizedMessage: ${it.localizedMessage}, cause: ${it.cause}")
                        val error = object : UIErrorInterface {
                            override val errorCode: ErrorCodeInterface = ErrorCode.CONNECTION_EXCEPTION
                            override val additionalParams: Map<String, String> = emptyMap()
                        }
                        _state = _state.asDoneLoadInitialData()
                            .withError(error)
                        state.value = _state
                    }
                    .collectLatest {
                        processStockProductsResponse(logTag, it)
                    }
                },
                onError = { error ->
                    _state = _state.asDoneLoadInitialData()
                        .withError(error)
                    state.value = _state
                }
            )
        }
    }

    private fun processStockProductsResponse(
        logTag: String,
        response: ApolloResponse<GetAllStockProductQuery.Data>
    ) {
        response.data?.let { response ->
            log("$logTag) success response: ${response.getAllStockProduct}")
            val products = response.getAllStockProduct.filterNotNull().map { product ->
                StockProduct(
                    productId = product.product.id,
                    productName = product.product.name,
                    productImage = product.product.image,
                    storeId = product.store.id,
                    styleColor = product.styleColor,
                    size = product.size,
                    sku = product.sku,
                    price = product.price,
                    quantity = product.quantity,
                )
            }
            val newModel = _state.data.copy(products = products)
            _state = _state.asDoneLoadInitialData()
                .withData(newModel)
            state.value = _state
        }
        response.errors?.let {
            log("$logTag) fail response: $it")
            val error = AddUIError(
                errorCode = ErrorCode.UNKNOWN_EXCEPTION,
                additionalParams = emptyMap()
            )
            _state = _state.asDoneLoadInitialData()
                .withError(error)
            state.value = _state
        }
    }

    fun requestCamera() {
        _state = _state.asRequestingCamera()
        state.value = _state
    }

    fun finishCapturing() {
        _state = _state.asDoneRequestCamera()
        state.value = _state
    }

    fun snackBarShown() {
        _state = _state
            .asIdle()
            .withError(null)
        state.value = _state
    }
}
