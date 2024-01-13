package com.dnovaes.stockcontrol.features.addproduct.models

import com.dnovaes.stockcontrol.ui.State


data class AddUIObservable(
    val state: State,
    val data: AddUIModel,
    val process: AddProcess
) {
    fun withProcess(process: AddProcess) = this.copy(process = process)

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
        state == State.INITIAL &&
        process == AddProcess.LOAD_INITIAL_DATA

    fun isRegisteringProduct() =
        state == State.PROCESSING &&
        process == AddProcess.ADD_PRODUCT_REQUEST
}

enum class AddProcess {
    ADD_PRODUCT_REQUEST,
    LOAD_INITIAL_DATA
}

