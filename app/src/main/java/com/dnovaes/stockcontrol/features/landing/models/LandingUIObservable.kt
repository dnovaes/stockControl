package com.dnovaes.stockcontrol.features.landing.models

import com.dnovaes.stockcontrol.common.models.State
import com.dnovaes.stockcontrol.common.models.UIErrorInterface
import com.dnovaes.stockcontrol.common.models.UIModelInterface
import com.dnovaes.stockcontrol.common.models.UIObservable
import com.dnovaes.stockcontrol.common.models.UIProcessInterface

data class LandingUIObservable(
    override val state: State,
    override val process: LandingProcess,
    override val data: LandingUIModel,
    override val error: UIErrorInterface? = null
): UIObservable<LandingUIModel>(state, process, data, error) {

    override fun withData(model: LandingUIModel): UIObservable<LandingUIModel>
            = copy(data = model)

    fun asLoadingAppData() = copy(
        state = State.PROCESSING,
        process = LandingProcess.LOAD_INITIAL_DATA,
    )

    fun asLoadedAppData() = copy(
        state = State.DONE,
        process = LandingProcess.LOAD_INITIAL_DATA
    )

    fun withError(error: UIErrorInterface?): LandingUIObservable
        = copy(error = error)

    fun isLoadingInitialData() =
        state == State.PROCESSING &&
                process == LandingProcess.LOAD_INITIAL_DATA

    fun asIdle(): LandingUIObservable {
        return copy(state = State.IDLE, process = LandingProcess.LOAD_INITIAL_DATA)
    }
}

class LandingUIModel: UIModelInterface {

}

enum class LandingProcess: UIProcessInterface {
    LOAD_INITIAL_DATA
}

