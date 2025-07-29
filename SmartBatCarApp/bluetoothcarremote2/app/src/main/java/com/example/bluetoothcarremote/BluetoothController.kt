package com.example.bluetoothcarremote

import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.io.OutputStream

object BluetoothController {
    var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    // Call this when socket is successfully connected
    fun setSocket(socket: BluetoothSocket) {
        bluetoothSocket = socket
        outputStream = try {
            socket.outputStream
        } catch (e: IOException) {
            Log.e("BluetoothController", "Error getting output stream", e)
            null
        }
    }

    // Sends a single character command over Bluetooth
    fun sendCommand(command: Char) {
        try {
            outputStream?.write(command.code)
            Log.d("BluetoothController", "Sent command: $command")
        } catch (e: IOException) {
            Log.e("BluetoothController", "Error sending command", e)
        }
    }

    // Gracefully close the socket and output stream
    fun closeConnection() {
        try {
            outputStream?.close()
            bluetoothSocket?.close()
        } catch (e: IOException) {
            Log.e("BluetoothController", "Error closing connection", e)
        } finally {
            outputStream = null
            bluetoothSocket = null
            Log.d("BluetoothController", "Bluetooth connection closed")
        }
    }
}
