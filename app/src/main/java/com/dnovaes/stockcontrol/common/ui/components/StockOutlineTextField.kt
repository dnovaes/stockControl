package com.dnovaes.stockcontrol.common.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.dnovaes.stockcontrol.ui.theme.defaultPadding

@Composable
fun ColumnScope.StockOutlineTextField(
    labelText: String,
    currentValue: String,
    enabled: Boolean = true,
    onValueChange: ((String) -> Unit)? = null
) {
    OutlinedTextField(
        value = currentValue,
        label = { Text(labelText) },
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = defaultPadding.dp, vertical = 4.dp),
        onValueChange = {
            onValueChange?.invoke(it)
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
    )
}

@Composable
fun RowScope.StockOutlineTextField(
    modifier: Modifier,
    labelText: String,
    currentValue: TextFieldValue,
    keyboardOptions: KeyboardOptions,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onValueChange: ((TextFieldValue) -> Unit)? = null
) {
    OutlinedTextField(
        value = currentValue,
        label = { Text(labelText) },
        modifier = modifier,
        onValueChange = {
            onValueChange?.invoke(it)
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        singleLine = true,
    )
}
