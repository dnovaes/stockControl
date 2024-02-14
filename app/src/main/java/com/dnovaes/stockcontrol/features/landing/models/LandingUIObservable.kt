package com.dnovaes.stockcontrol.features.landing.models

import com.dnovaes.stockcontrol.common.models.UIErrorInterface
import com.dnovaes.stockcontrol.ui.State

data class LandingUIObservable(
    private val state: State,
    private val process: LandingProcess,
    private val data: LandingUIModel,
    private val error: UIErrorInterface? = null
) {
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
}

class LandingUIModel {

}

enum class LandingProcess {
    LOAD_INITIAL_DATA
}

