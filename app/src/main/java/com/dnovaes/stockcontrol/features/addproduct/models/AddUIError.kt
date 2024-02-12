package com.dnovaes.stockcontrol.features.addproduct.models

import com.dnovaes.stockcontrol.common.models.ErrorCodeInterface
import com.dnovaes.stockcontrol.common.models.UIErrorInterface

data class AddUIError(
    override val errorCode: ErrorCodeInterface,
    override val additionalParams: Map<String, String>
): UIErrorInterface