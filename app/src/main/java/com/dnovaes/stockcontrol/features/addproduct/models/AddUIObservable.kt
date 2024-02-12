package com.dnovaes.stockcontrol.features.addproduct.models

import com.dnovaes.stockcontrol.common.models.UIErrorInterface
import com.dnovaes.stockcontrol.ui.State


data class AddUIObservable(
    val state: State,
    val data: AddUIModel,
    val process: AddProcess,
    val error: UIErrorInterface? = null
) {
    fun withProcess(process: AddProcess) = this.copy(process = process)
    fun withError(error: UIErrorInterface) = this.copy(error = error)

    fun asAddingProduct(): AddUIObservable {
        return copy(
            state = State.PROCESSING,
            process = AddProcess.ADD_PRODUCT_REQUEST
        )
    }

    fun asDoneAddProduct(): AddUIObservable {
        return copy(
            state = State.DONE,
            process = AddProcess.ADD_PRODUCT_REQUEST
        )
    }



    fun isInitialLoadScreen() =
        state == State.START &&
        process == AddProcess.LOAD_INITIAL_DATA

    fun isRegisteringProduct() =
        state == State.PROCESSING &&
        process == AddProcess.ADD_PRODUCT_REQUEST

    fun isDoneProductRegistration() =
        state == State.DONE &&
        process == AddProcess.ADD_PRODUCT_REQUEST

}

enum class AddProcess {
    ADD_PRODUCT_REQUEST,
    LOAD_INITIAL_DATA
}

