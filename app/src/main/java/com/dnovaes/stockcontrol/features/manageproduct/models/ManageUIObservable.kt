package com.dnovaes.stockcontrol.features.manageproduct.models

import com.dnovaes.stockcontrol.common.models.State
import com.dnovaes.stockcontrol.common.models.UIErrorInterface
import com.dnovaes.stockcontrol.common.models.UIObservable
import com.dnovaes.stockcontrol.common.models.UIProcessInterface


data class ManageUIObservable(
    override val state: State,
    override val process: ManageProcess,
    override val data: ManageUIModel,
    override val error: UIErrorInterface? = null
): UIObservable<ManageUIModel>(state, process, data, error) {

    fun withProcess(process: ManageProcess) = this.copy(process = process)

    fun withError(error: UIErrorInterface?): ManageUIObservable
        = copy(error = error)

    override fun withData(model: ManageUIModel) = this.copy(data = model)

    fun asIdle()
        = copy(state = State.IDLE, process = ManageProcess.LOAD_INITIAL_DATA)

    fun asLoadingInitialData(): ManageUIObservable
        = copy(state = State.PROCESSING, process = ManageProcess.LOAD_INITIAL_DATA)

    fun asDoneLoadInitialData(): ManageUIObservable
            = copy(state = State.DONE, process = ManageProcess.LOAD_INITIAL_DATA)

    fun isLoadingInitialData() =
        state == State.PROCESSING && process == ManageProcess.LOAD_INITIAL_DATA

    fun isDoneLoadInitialData() =
        state == State.DONE && process == ManageProcess.LOAD_INITIAL_DATA

    fun asRequestingCamera() = copy(
        state = State.PROCESSING,
        process = ManageProcess.REQUEST_CAMERA
    )

    fun isRequestingCamera() =
        state == State.PROCESSING &&
                process == ManageProcess.REQUEST_CAMERA

    fun asDoneRequestCamera()
        = copy(state = State.DONE, process = ManageProcess.REQUEST_CAMERA)
}

enum class ManageProcess: UIProcessInterface {
    LOAD_INITIAL_DATA,
    REQUEST_CAMERA
}
