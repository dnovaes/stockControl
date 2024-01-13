package com.dnovaes.stockcontrol.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.dnovaes.stockcontrol.ui.theme.AnnePrimary

@Composable
fun LoadingOverlay(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = SolidColor(Color.White),
                alpha = 0.8f,
            )
            .zIndex(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(50.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            color = AnnePrimary,
            fontSize = 28.sp
        )
    }
}
