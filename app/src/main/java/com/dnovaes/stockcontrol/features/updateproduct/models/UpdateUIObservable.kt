package com.dnovaes.stockcontrol.features.updateproduct.models

import com.dnovaes.stockcontrol.common.models.State
import com.dnovaes.stockcontrol.common.models.UIErrorInterface
import com.dnovaes.stockcontrol.common.models.UIObservable
import com.dnovaes.stockcontrol.common.models.UIProcessInterface


data class UpdateUIObservable(
    override val state: State,
    override val process: UpdateProcess,
    override val data: UpdateUIModel,
    override val error: UIErrorInterface? = null
): UIObservable<UpdateUIModel>(state, process, data, error) {

    fun withProcess(process: UpdateProcess) = this.copy(process = process)

    fun withError(error: UIErrorInterface?): UpdateUIObservable
        = copy(error = error)

    override fun withData(model: UpdateUIModel) = this.copy(data = model)

    fun asLoadingInitialData(): UpdateUIObservable
        = copy(state = State.PROCESSING, process = UpdateProcess.LOAD_INITIAL_DATA)

    fun asDoneLoadInitialData(): UpdateUIObservable
            = copy(state = State.DONE, process = UpdateProcess.LOAD_INITIAL_DATA)

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
        state == State.START && process == UpdateProcess.LOAD_INITIAL_DATA

    fun isUpdatingProduct() =
        state == State.PROCESSING &&
                process == UpdateProcess.UPDATE_PRODUCT_REQUEST

    fun isDoneUpdateProduct() =
        state == State.DONE && process == UpdateProcess.UPDATE_PRODUCT_REQUEST

}

enum class UpdateProcess: UIProcessInterface {
    UPDATE_PRODUCT_REQUEST,
    LOAD_INITIAL_DATA
}

