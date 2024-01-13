package com.dnovaes.stockcontrol.common.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.dnovaes.stockcontrol.ui.theme.defaultPadding

@ExperimentalMaterial3Api
@Composable
fun ColumnScope.StockOutlineTextField(
    labelText: String,
    currentValue: String,
    onValueChange: ((String) -> Unit)? = null
) {
    OutlinedTextField(
        value = currentValue,
        label = { Text(labelText) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = defaultPadding.dp, vertical = 4.dp),
        onValueChange = {
            onValueChange?.invoke(it)
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
    )
}
