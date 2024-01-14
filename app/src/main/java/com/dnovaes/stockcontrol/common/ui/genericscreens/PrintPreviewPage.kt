package com.dnovaes.stockcontrol.common.ui.genericscreens

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dnovaes.stockcontrol.R
import com.dnovaes.stockcontrol.common.ui.components.StockButton
import com.dnovaes.stockcontrol.common.ui.components.StockNegativeButton
import com.dnovaes.stockcontrol.ui.icons.printerFilledIcon
import com.dnovaes.stockcontrol.ui.theme.AnnePrimary

@Composable
fun PrintPreviewPage(
    onPositiveButtonClick: () -> Unit,
    onNegativeButtonClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Icon(
            imageVector = printerFilledIcon(),
            contentDescription = "Header icon",
            modifier = Modifier
                .size(80.dp),
            tint = AnnePrimary,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.preview_print_label_title),
            color = AnnePrimary,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            maxLines = 2,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                vertical = 10.dp,
                horizontal = 80.dp
            ),
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(id = R.string.empty),
            color = AnnePrimary,
            fontSize = 16.sp,
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
                stringResource(R.string.print_label_bt_title),
                Modifier.padding(vertical = 4.dp)
            ) {
                onPositiveButtonClick()
            }

            StockNegativeButton(
                stringResource(R.string.generic_success_page_negative_bt_label),
                Modifier.padding(vertical = 4.dp)
            ) {
                onNegativeButtonClick()
            }
        }
    }
}
