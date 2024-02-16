package com.dnovaes.stockcontrol.common.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.dnovaes.stockcontrol.ui.theme.AnneBackground
import com.dnovaes.stockcontrol.ui.theme.AnnePrimary
import com.dnovaes.stockcontrol.ui.theme.AnnePrimaryDisabled


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDropdownField(
    label: Int,
    value: String,
    modifier: Modifier,
    colors: TextFieldColors,
    readOnly: Boolean,
    enabled: Boolean,
    textStyle: TextStyle = LocalTextStyle.current,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onClick: (() -> Unit)? = null,
) {

    // If color is not provided via the text style, use content color as a default
    val textColor = if (enabled) {
        AnnePrimary
    } else {
        AnnePrimaryDisabled
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    CompositionLocalProvider(LocalTextSelectionColors provides TextSelectionColors(handleColor = Color.Black, backgroundColor = Color.White)) {
        BasicTextField(
            value = value,
            modifier = modifier
                // Merge semantics at the beginning of the modifier chain to ensure padding is
                // considered part of the text field.
                .semantics(mergeDescendants = true) {}
                .padding(top = 8.dp)
                .defaultMinSize(
                    minWidth = OutlinedTextFieldDefaults.MinWidth,
                    minHeight = OutlinedTextFieldDefaults.MinHeight
                ),
            onValueChange = { },
            enabled = enabled,
            readOnly = readOnly,
            textStyle = mergedTextStyle,
            cursorBrush = SolidColor(Color.Black),
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions.Default,
            keyboardActions = KeyboardActions.Default,
            interactionSource = interactionSource,
            singleLine = true,
            maxLines = 1,
            minLines = 1,
            decorationBox = @Composable { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = value,
                    visualTransformation = visualTransformation,
                    innerTextField = innerTextField,
                    placeholder = null,
                    label = { Text(stringResource(id = label)) },
                    leadingIcon = null,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "",
                        )
                    },
                    prefix = null,
                    suffix = null,
                    supportingText = null,
                    singleLine = true,
                    enabled = enabled,
                    isError = false,
                    interactionSource = interactionSource,
                    colors = colors,
                    container = {
                        val shape = RoundedCornerShape(4.dp)
                        val borderColor = if (enabled) {
                            AnnePrimary
                        } else {
                            colors.disabledIndicatorColor
                        }
                        Box(
                            Modifier
                                .border(
                                    border = BorderStroke(1.dp, borderColor),
                                    shape = shape
                                )
                                .background(AnneBackground, shape)
                                .clickable {
                                    onClick?.invoke()
                                }
                        )
                    }
                )
            }
        )
    }
}
