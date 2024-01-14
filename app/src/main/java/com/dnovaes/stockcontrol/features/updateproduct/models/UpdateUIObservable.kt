package com.dnovaes.stockcontrol.features.updateproduct.models

import com.dnovaes.stockcontrol.ui.State


data class UpdateUIObservable(
    val state: State,
    val data: UpdateUIModel,
    val process: UpdateProcess
) {
    fun withProcess(process: UpdateProcess) = this.copy(process = process)

    fun asUpdatingProduct(): UpdateUIObservable {
        return copy(
            state = State.PROCESSING,
            process = UpdateProcess.UPDATE_PRODUCT_REQUEST
        )
    }

    fun asDoneUpdateProduct(): UpdateUIObservable {
        return copy(
            state = State.DONE,
            process = UpdateProcess.UPDATE_PRODUCT_REQUEST
        )
    }

    fun isInitialLoadScreen() =
        state == State.INITIAL && process == UpdateProcess.LOAD_INITIAL_DATA

    fun isUpdatingProduct() =
        state == State.PROCESSING &&
                process == UpdateProcess.UPDATE_PRODUCT_REQUEST

    fun isDoneUpdateProduct() =
        state == State.DONE && process == UpdateProcess.UPDATE_PRODUCT_REQUEST

}

enum class UpdateProcess {
    UPDATE_PRODUCT_REQUEST,
    LOAD_INITIAL_DATA
}

