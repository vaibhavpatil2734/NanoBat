package com.example.bluetoothcarremote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

class ControlActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DPadControls()
                }
            }
        }
    }
}

@Composable
fun DPadControls() {
    val buttonSize = 60.dp  // Reduced size

    Box(
        modifier = Modifier
            .size(180.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Up (Forward)
        DPadButton(
            modifier = Modifier
                .size(buttonSize)
                .align(Alignment.TopCenter),
            onPress = { BluetoothController.sendCommand('1') },
            onRelease = { BluetoothController.sendCommand('0') }
        )

        // Down (Backward)
        DPadButton(
            modifier = Modifier
                .size(buttonSize)
                .align(Alignment.BottomCenter),
            onPress = { BluetoothController.sendCommand('2') },
            onRelease = { BluetoothController.sendCommand('0') }
        )

        // Left
        DPadButton(
            modifier = Modifier
                .size(buttonSize)
                .align(Alignment.CenterStart),
            onPress = { BluetoothController.sendCommand('3') },
            onRelease = { BluetoothController.sendCommand('0') }
        )

        // Right
        DPadButton(
            modifier = Modifier
                .size(buttonSize)
                .align(Alignment.CenterEnd),
            onPress = { BluetoothController.sendCommand('4') },
            onRelease = { BluetoothController.sendCommand('0') }
        )
    }
}

@Composable
fun DPadButton(
    modifier: Modifier,
    onPress: () -> Unit,
    onRelease: () -> Unit
) {
    Box(
        modifier = modifier
            .border(BorderStroke(2.dp, Color.White), CircleShape)
            .background(Color.Transparent, CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onPress()
                        tryAwaitRelease()
                        onRelease()
                    }
                )
            }
    )
}
