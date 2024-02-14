package com.dnovaes.stockcontrol.common.models


abstract class UIObservable<M: UIModelInterface>(
    open val state: State,
    open val process: UIProcessInterface,
    open val data: M,
    open val error: UIErrorInterface?
) {
    abstract fun withData(model: M): UIObservable<M>
}
