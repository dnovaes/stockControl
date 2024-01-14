package com.dnovaes.stockcontrol.common.ui.genericscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dnovaes.stockcontrol.R
import com.dnovaes.stockcontrol.common.ui.components.StockButton
import com.dnovaes.stockcontrol.common.ui.components.StockNegativeButton
import com.dnovaes.stockcontrol.ui.theme.AnnePrimary

@Composable
fun FullScreenAlert(
    headerIcon: ImageVector,
    title: Int,
    subtitle: Int?,
    positiveButtonLabel: Int,
    negativeButtonLabel: Int,
    onPositiveButtonClick: () -> Unit,
    onNegativeButtonClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(140.dp))
        Icon(
            imageVector = headerIcon,
            contentDescription = "Header icon",
            modifier = Modifier
                .size(90.dp),
            tint = AnnePrimary,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = title),
            color = AnnePrimary,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            maxLines = 2,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                vertical = 10.dp,
                horizontal = 60.dp
            ),
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(id = R.string.empty),
            color = AnnePrimary,
            fontSize = 16.sp,
            lineHeight = 26.sp,
            maxLines = 2,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(20.dp),
        )
        Spacer(modifier = Modifier.height(40.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            StockButton(
                stringResource(positiveButtonLabel),
                Modifier.padding(vertical = 4.dp)
            ) {
                onPositiveButtonClick()
            }

            StockNegativeButton(
                stringResource(negativeButtonLabel),
                Modifier.padding(vertical = 4.dp)
            ) {
                onNegativeButtonClick()
            }
        }
    }
}
