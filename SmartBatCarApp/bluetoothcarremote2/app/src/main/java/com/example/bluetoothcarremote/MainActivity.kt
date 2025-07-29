package com.example.bluetoothcarremote

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bluetoothcarremote.ui.theme.BluetoothcarremoteTheme
import java.util.*

class MainActivity : ComponentActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private val deviceList = mutableStateListOf<BluetoothDevice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        requestBluetoothPermissions()

        setContent {
            BluetoothcarremoteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    BluetoothDeviceList(Modifier.padding(padding))
                }
            }
        }

        if (hasBluetoothPermission()) {
            startBluetoothScan()
        }
    }

    private fun requestBluetoothPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }

        ActivityCompat.requestPermissions(this, permissions, 1)
    }

    private fun hasBluetoothPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }

    private fun startBluetoothScan() {
        if (!hasBluetoothPermission()) return

        deviceList.clear()
        bluetoothAdapter.bondedDevices?.forEach { device ->
            deviceList.add(device)
        }
    }

    @Composable
    fun BluetoothDeviceList(modifier: Modifier = Modifier) {
        Column(modifier = modifier.padding(16.dp)) {
            Text("Select a Bluetooth device", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(deviceList) { device ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                connectToDevice(device)
                            }
                    ) {
                        Text(
                            text = "${device.name ?: "Unknown"} (${device.address})",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }

    private fun connectToDevice(device: BluetoothDevice) {
        if (!hasBluetoothPermission()) {
            Toast.makeText(this, "Bluetooth permission not granted", Toast.LENGTH_SHORT).show()
            return
        }

        val uuid = try {
            device.uuids?.getOrNull(0)?.uuid
        } catch (e: Exception) {
            null
        } ?: UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

        val socket: BluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)

        Thread {
            try {
                bluetoothAdapter.cancelDiscovery()
                socket.connect()
                BluetoothController.setSocket(socket)

                runOnUiThread {
                    startActivity(Intent(this, ControlActivity::class.java))
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Connection failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}
