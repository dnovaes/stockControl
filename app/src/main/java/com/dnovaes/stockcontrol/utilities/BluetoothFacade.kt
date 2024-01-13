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

    companion object {
        const val BLUETOOTH_PERMISSION_CODE = 123
    }

    private val adapter: BluetoothAdapter? =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    private var currentPairedDevice: BluetoothDevice? = null

    private val scope = CoroutineScope(Dispatchers.IO)

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

    fun connectToD110Printer(context: Context) {
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
                // Connection failed
                // Handle accordingly
                log("Printer Connection failed with exception: $e")
            }
        }
    }

    private var niimbotPrinterClient: NiimbotPrinterClient? = null

    fun printWithClient(image: Bitmap) {
        val address = currentPairedDevice?.address ?: return
        val adapter = adapter ?: return

        scope.launch(Dispatchers.IO) {
            val client = niimbotPrinterClient ?: run {
                niimbotPrinterClient = NiimbotPrinterClient(
                    address = address,
                    bluetoothAdapter = adapter
                )
                niimbotPrinterClient
            }

            //log(client?.getPrintStatus().toString())
            val bitmap: Bitmap? = image //BitmapFactory.decodeResource(context.resources, R.mipmap.hello)
            val drawable = context.getDrawable(R.mipmap.hello)!!
            log("drawable: width: ${drawable.minimumWidth}, height: ${drawable.minimumHeight}")
            //val bitmap: Bitmap? = createBitmapWithText()
            bitmap?.let {
                val width = 192
                val height = 96
                log("Printing with width: $width, height: $height")
                client?.printLabel(it, width, height, labelDensity = 1)
            }
        }
    }

    fun createBitmapWithText(): Bitmap {
        val bitmapWidth = 28
        val bitmapHeight = 14
        val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Fill the background with white color
        canvas.drawColor(Color.WHITE)

        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 40f
            textAlign = Paint.Align.CENTER
        }

        val text = "Hello, world!"
        val textX = (bitmapWidth / 2).toFloat()
        val textY = (bitmapHeight / 2).toFloat()

        canvas.drawText(text, textX, textY, paint)

        return bitmap
    }

}