package com.dnovaes.stockcontrol.features.landing.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.dnovaes.stockcontrol.GetAllCategoriesQuery
import com.dnovaes.stockcontrol.common.models.ErrorCode
import com.dnovaes.stockcontrol.common.models.ErrorCodeInterface
import com.dnovaes.stockcontrol.common.models.State
import com.dnovaes.stockcontrol.common.models.UIErrorInterface
import com.dnovaes.stockcontrol.common.models.business.ProductCategory
import com.dnovaes.stockcontrol.common.monitoring.log
import com.dnovaes.stockcontrol.common.monitoring.logFailedRequest
import com.dnovaes.stockcontrol.common.monitoring.logSentRequest
import com.dnovaes.stockcontrol.common.monitoring.logSuccessRequest
import com.dnovaes.stockcontrol.common.utils.SessionManager
import com.dnovaes.stockcontrol.features.addproduct.models.AddUIError
import com.dnovaes.stockcontrol.features.landing.models.LandingProcess
import com.dnovaes.stockcontrol.features.landing.models.LandingUIModel
import com.dnovaes.stockcontrol.features.landing.models.LandingUIObservable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val apolloClient: ApolloClient
): ViewModel() {

    private val initialObservable = LandingUIObservable(
        state = State.START,
        process = LandingProcess.LOAD_INITIAL_DATA,
        data = LandingUIModel(),
        error = null
    )

    private var _uiState = initialObservable
    val state: MutableState<LandingUIObservable> = mutableStateOf(_uiState)

    fun resetState() {
        _uiState = initialObservable
        state.value = _uiState
    }

    fun loadCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState = _uiState.asLoadingAppData()
            state.value = _uiState

            val logTag = "GetAllCategories"
            try {
                logSentRequest(tag = logTag, message = "process: ${_uiState.process}, state: ${_uiState.state}")
                val flow = apolloClient.query(GetAllCategoriesQuery()).toFlow()
                withTimeout(8000) {
                    flow.catch {
                        logFailedRequest(tag = logTag, message = "localizedMessage: ${it.localizedMessage}, cause: ${it.cause}")
                        val error = object : UIErrorInterface {
                            override val errorCode: ErrorCodeInterface = ErrorCode.CONNECTION_EXCEPTION
                            override val additionalParams: Map<String, String> = emptyMap()
                        }
                        _uiState = _uiState.asLoadedAppData()
                            .withError(error)
                        state.value = _uiState
                    }
                    .collectLatest {
                        processCategoriesResponse(logTag, it)
                    }
                }
            } catch (e: TimeoutCancellationException) {
                log("$logTag) ${e.stackTraceToString()}")
                val error = object : UIErrorInterface {
                    override val errorCode: ErrorCodeInterface = ErrorCode.TIME_OUT_EXCEPTION
                    override val additionalParams: Map<String, String> = emptyMap()
                }
                _uiState = _uiState.asLoadedAppData()
                    .withError(error)
                state.value = _uiState
            } catch (e: Exception) {
                log("$logTag) Exception: ${e.localizedMessage}, ${e.stackTraceToString()}")
                val error = object : UIErrorInterface {
                    override val errorCode: ErrorCodeInterface = ErrorCode.UNKNOWN_EXCEPTION
                    override val additionalParams: Map<String, String> = emptyMap()
                }
                _uiState = _uiState.asLoadedAppData()
                    .withError(error)
                state.value = _uiState
            }
        }
    }

    private fun processCategoriesResponse(
        logTag: String,
        response: ApolloResponse<GetAllCategoriesQuery.Data>
    ) {
        response.data?.let {
            logSuccessRequest(logTag, "${it.getAllCategories}")
            val products = it.getAllCategories.map {
                    product -> ProductCategory(product.id, product.name)
            }
            SessionManager.saveProductCategories(products)
            _uiState = _uiState.asLoadedAppData()
        }
        response.errors?.let {
            log("$logTag) fail response: $it")
            val error = AddUIError(
                errorCode = ErrorCode.UNKNOWN_EXCEPTION,
                additionalParams = emptyMap()
            )
            _uiState = _uiState.asLoadedAppData()
                .withError(error)
        }
        state.value = _uiState
    }


    fun snackBarShown() {
        _uiState = _uiState
            .asIdle()
            .withError(null)
        state.value = _uiState
    }
}
