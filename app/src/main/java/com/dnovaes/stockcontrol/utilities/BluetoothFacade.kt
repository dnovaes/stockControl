package com.dnovaes.stockcontrol.utilities

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.app.ActivityCompat
import com.dnovaes.stockcontrol.R
import com.dnovaes.stockcontrol.common.NiimbotPrinterClient
import com.dnovaes.stockcontrol.common.monitoring.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class BluetoothFacade(
    private val context: Context
) {

    var isConnected = false

    companion object {
        const val BLUETOOTH_PERMISSION_CODE = 123
    }

    private val adapter: BluetoothAdapter? =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    private var currentPairedDevice: BluetoothDevice? = null

    fun scanPairedDevices(
        onFailPermission: () -> Unit,
        onBluetoothNotEnabled: () -> Unit
    ) {
        if (adapter == null) {
            // Device doesn't support Bluetooth
            log("Bluetooth not supported on this device")
            return
        }

        if (!adapter.isEnabled) {
            // Bluetooth is not enabled, prompt the user to enable it
            log("Bluetooth is not enabled. Please enable it to proceed.")
            onBluetoothNotEnabled.invoke()
            return
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            log("Permission to access bluetooth devices not allowed.")
            onFailPermission.invoke()
            return
        }

        val pairedDevices: Set<BluetoothDevice> = adapter.bondedDevices
        log("=====================")
        if (pairedDevices.isEmpty()) {
            log("No paired Bluetooth devices found.")
        } else {
            log("Paired Bluetooth devices:")
            for (device in pairedDevices) {
                if (device.name.startsWith("D110")) {
                    currentPairedDevice = device
                }
                log("Device: ${device.name}, Address: ${device.address}")
            }
        }
        log("=====================")
    }

    fun connectToD110Printer() {
        val address = currentPairedDevice?.address ?: return
        val adapter = adapter ?: return

        niimbotPrinterClient ?: run {
            niimbotPrinterClient = NiimbotPrinterClient(
                address = address,
                bluetoothAdapter = adapter
            )
            niimbotPrinterClient
        }
/*
        val pairedDevice = currentPairedDevice ?: run {
            log("Printer D110 is not paired yet.")
            return
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            log("(D110) Printer Connection: Need to request bluetooth permission")
            return
        }

        val deviceUUID = pairedDevice.uuids.firstOrNull()?.uuid ?: UUID.randomUUID()
        log("Connecting to ${pairedDevice.name} with address ${pairedDevice.address} and UUID: $deviceUUID")
        scope.launch(Dispatchers.IO) {
            val socket = pairedDevice.createRfcommSocketToServiceRecord(deviceUUID)
            try {
                if (!socket.isConnected) {
                    socket.connect()
                    isConnected = true
                    log("Printer Connection successful.")
                }

                log("Sending data to printer.")
                val outputStream = socket.outputStream

                // Convert text to bytes using the appropriate character set (usually UTF-8)
                var printData = "SIZE 3.2\nGAP 0.12\n".toByteArray(Charsets.UTF_8)
                outputStream.write(printData)
                outputStream.flush() // Flush the stream to ensure data is sent

                printData = "TEXT 200,100, \"3\",0,1,1,\"Hello, Printer\"\n".toByteArray(Charsets.UTF_8) // Include '\n' for a line break
                outputStream.write(printData)

                printData = "PRINT 1\n".toByteArray(Charsets.UTF_8) // Include '\n' for a line break
                outputStream.write(printData)

                printData = "END\n".toByteArray(Charsets.UTF_8) // Include '\n' for a line break
                outputStream.write(printData)

                outputStream.flush() // Flush the stream to ensure data is sent
            } catch (e: IOException) {
                isConnected = false
                // Connection failed
                // Handle accordingly
                log("Printer Connection failed with exception: $e")
            }
        }
*/
    }

    private var niimbotPrinterClient: NiimbotPrinterClient? = null

    suspend fun printWithClient(image: Bitmap, onSuccessfulPrint: () -> Unit) {
        //log(client?.getPrintStatus().toString())
        //BitmapFactory.decodeResource(context.resources, R.mipmap.hello)
        //val bitmap: Bitmap? = createBitmapWithText()
        val width = 192
        val height = 96
        log("Printing with width: $width, height: $height")
        niimbotPrinterClient?.printLabel(
            image,
            width,
            height,
            labelDensity = 1,
            onSuccessfulPrint = onSuccessfulPrint
        )
    }

}
