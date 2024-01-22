package com.dnovaes.stockcontrol.common.models

import com.dnovaes.stockcontrol.ui.State

abstract class UIObservable<T>(
    open val state: State,
    open val process: UIProcessInterface,
    open val data: UIModelInterface,
    open val error: UIErrorInterface?
)
