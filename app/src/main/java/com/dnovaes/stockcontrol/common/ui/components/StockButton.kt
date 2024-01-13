package com.dnovaes.stockcontrol.common.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.dnovaes.stockcontrol.ui.theme.AnneBackground
import com.dnovaes.stockcontrol.ui.theme.AnnePrimary
import com.dnovaes.stockcontrol.ui.theme.defaultPadding


@Composable
fun ColumnScope.StockButton(
    textContent: String,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = AnnePrimary,
        contentColor = AnneBackground
    ),
    onClick: (() -> Unit)? = null
) {
    Button(
        onClick = { onClick?.invoke() },
        colors = colors,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .align(Alignment.CenterHorizontally)
            .padding(horizontal = defaultPadding.dp, vertical = 4.dp),
        border = BorderStroke(1.dp, SolidColor(AnnePrimary))
    ) {
        Text(text = textContent)
    }
}

@Composable
fun ColumnScope.StockNegativeButton(
    textContent: String,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = AnneBackground,
        contentColor = AnnePrimary
    ),
    onClick: (() -> Unit),
) {
   StockButton(
       textContent = textContent,
       modifier = modifier,
       colors = colors,
       onClick = onClick
   )
}
