package com.dnovaes.stockcontrol.features.updateproduct.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.dnovaes.stockcontrol.common.models.ErrorCode
import com.dnovaes.stockcontrol.common.models.ErrorCodeInterface
import com.dnovaes.stockcontrol.common.models.State
import com.dnovaes.stockcontrol.common.models.UIErrorInterface
import com.dnovaes.stockcontrol.common.utils.SessionManager
import com.dnovaes.stockcontrol.features.updateproduct.models.UpdateProcess
import com.dnovaes.stockcontrol.features.updateproduct.models.UpdateUIModel
import com.dnovaes.stockcontrol.features.updateproduct.models.UpdateUIObservable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    fun updateProduct() {
        viewModelScope.launch(Dispatchers.IO) {
            _updateState = _updateState.asUpdatingProduct()
            updateState.value = _updateState

            delay(2000)

            _updateState = _updateState.asDoneUpdateProduct()
            updateState.value = _updateState
        }
    }

    fun loadProductInfo(productId: String) {
        _updateState = _updateState.asLoadingInitialData()
        updateState.value = _updateState

        viewModelScope.launch {
            delay(2000)

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
