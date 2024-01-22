package com.dnovaes.stockcontrol.common.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.dnovaes.stockcontrol.common.monitoring.log
import com.dnovaes.stockcontrol.utilities.StockBluetoothManager
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

    protected val bluetoothManager: StockBluetoothManager by lazy {
        StockBluetoothManager(this.applicationContext)
    }

    protected fun scanPairedDevices() {
        bluetoothManager.scanPairedDevices(
            onBluetoothNotEnabled = {
                requestBluetoothFeature()
            },
            onFailPermission = {
                requestBluetoothPermission()
            }
        )
    }

    fun connectToPrinter() {
        lifecycleScope.launch(Dispatchers.IO) {
            bluetoothManager.connectToD110Printer()
            bluetoothManager.updatePrinterStatus()
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
            StockBluetoothManager.BLUETOOTH_PERMISSION_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestBluetoothFeature() {

        when {
            checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED -> {
                //sendBroadcast(Intent(ACTION_PERMISSIONS_GRANTED))
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                resultLauncher.launch(enableBluetoothIntent)
            }
            else -> {
                requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == StockBluetoothManager.BLUETOOTH_PERMISSION_CODE) {
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