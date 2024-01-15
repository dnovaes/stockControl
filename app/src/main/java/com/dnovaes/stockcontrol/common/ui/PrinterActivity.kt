package com.dnovaes.stockcontrol.common.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.dnovaes.stockcontrol.common.monitoring.log
import com.dnovaes.stockcontrol.utilities.BluetoothFacade
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class PrinterActivity: BaseActivity() {

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Bluetooth is enabled, proceed with connecting to a paired device
            scanPairedDevices()
        } else {
            // User didn't enable Bluetooth, handle accordingly
        }
    }

    private val bluetoothFacade: BluetoothFacade by lazy {
        BluetoothFacade(this.applicationContext)
    }

    protected fun scanPairedDevices() {
        bluetoothFacade.scanPairedDevices(
            onBluetoothNotEnabled = {
                requestBluetoothFeature()
            },
            onFailPermission = {
                requestBluetoothPermission()
            }
        )
    }

    fun connectToPrinter() {
        bluetoothFacade.connectToD110Printer()
    }

    fun printWithNiimbotClient(bitmap: Bitmap, onSuccessfulPrint: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            bluetoothFacade.printWithClient(bitmap, onSuccessfulPrint)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestBluetoothPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
            ),
            BluetoothFacade.BLUETOOTH_PERMISSION_CODE
        )
    }

    private fun requestBluetoothFeature() {
        val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        resultLauncher.launch(enableBluetoothIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == BluetoothFacade.BLUETOOTH_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, perform Bluetooth-related operations
                log("Bluetooth permission allowed")
            } else {
                // Permission denied, handle accordingly
                log("Bluetooth permission denied")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}