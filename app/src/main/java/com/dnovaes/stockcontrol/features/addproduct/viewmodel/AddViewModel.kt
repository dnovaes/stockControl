package com.dnovaes.stockcontrol.features.addproduct.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.dnovaes.stockcontrol.AddProductMutation
import com.dnovaes.stockcontrol.GetAllCompaniesQuery
import com.dnovaes.stockcontrol.common.models.ErrorCode
import com.dnovaes.stockcontrol.common.monitoring.log
import com.dnovaes.stockcontrol.common.utils.SessionManager
import com.dnovaes.stockcontrol.features.addproduct.models.AddProcess
import com.dnovaes.stockcontrol.features.addproduct.models.AddUIError
import com.dnovaes.stockcontrol.features.addproduct.models.AddUIModel
import com.dnovaes.stockcontrol.features.addproduct.models.AddUIObservable
import com.dnovaes.stockcontrol.type.NewProduct
import com.dnovaes.stockcontrol.ui.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddViewModel(
    private val apolloClient: ApolloClient
): ViewModel() {

    private val initialObservable = AddUIObservable(
        state = State.START,
        process = AddProcess.LOAD_INITIAL_DATA,
        data = AddUIModel(
            categories = SessionManager.loadProductCategories()
        ),
    )

    private var _addState = initialObservable
    val addState: MutableState<AddUIObservable> = mutableStateOf(_addState)

    fun resetState() {
        _addState = initialObservable
        addState.value = _addState
    }

    fun finishImageCapturing(image: Bitmap?) {
        val model = _addState.data
        val newModel = model.copy(registerImage = image)

        _addState = _addState.copy(data = newModel)
        addState.value = _addState
    }

    fun registerProduct(product: NewProduct) {
        val categoryId = product.categoryId
        val mappedProduct = product.copy(categoryId = categoryId)

        viewModelScope.launch(Dispatchers.IO) {
            _addState = _addState.asAddingProduct()
            addState.value = _addState

            apolloClient.mutation(AddProductMutation(mappedProduct))
                .toFlow()
                .catch {
                   log("localizedMessage: ${it.localizedMessage}, cause: ${it.cause}")
                }
                .collectLatest {
                    processRegisterProductResponse(it)
                }
        }
    }

    private fun processRegisterProductResponse(response: ApolloResponse<AddProductMutation.Data>) {
        response.data?.let {
            log("registerProduct) success response: ${it.createProduct}")
            _addState = _addState.asDoneAddProduct()
        }
        response.errors?.let {
            log("registerProduct) fail response: $it")
            val error = AddUIError(
                errorCode = ErrorCode.UNKNOWN_EXCEPTION,
                additionalParams = emptyMap()
            )
            _addState = _addState.asDoneAddProduct()
                .withError(error)
        }
        addState.value = _addState
    }

    fun getAllCompanies() {
        viewModelScope.launch(Dispatchers.IO) {
            _addState = _addState.asAddingProduct()
            addState.value = _addState

            try {
                val response = apolloClient.query(GetAllCompaniesQuery()).execute()
                response.data?.let {
                    log("registerProduct) success response: ${it.getAllCompanies}")
                }
                response.errors?.let {
                    log("registerProduct) fail response: $it")
                }
                delay(2000)
                _addState = _addState.asDoneAddProduct()
            } catch (e: Exception) {
                log("add-exception: ${e.localizedMessage}, ${e.stackTraceToString()}")
                val error = AddUIError(
                    errorCode = ErrorCode.UNKNOWN_EXCEPTION,
                    additionalParams = emptyMap()
                )
                _addState = _addState.asDoneAddProduct()
                    .withError(error)
            }
            addState.value = _addState
        }
    }
}
