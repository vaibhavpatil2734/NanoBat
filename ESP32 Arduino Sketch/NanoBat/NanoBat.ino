#include "BluetoothSerial.h"

BluetoothSerial SerialBT;

// Motor control pins
const int IN1 = 27;  // Left motor forward
const int IN2 = 26;  // Left motor backward
const int IN3 = 25;  // Right motor forward
const int IN4 = 33;  // Right motor backward

// LED pins
const int LED_HEARTBEAT = 2;  // On-board LED (blinks based on BT status)
const int LED_INPUT = 4;      // Input indicator (blinks when command received)

unsigned long lastBlinkTime = 0;
bool heartbeatState = false;
bool hasShownConnectionBlink = false;

void setup() {
  SerialBT.begin("ESP32_CAR");

  // Motor pins
  pinMode(IN1, OUTPUT);
  pinMode(IN2, OUTPUT);
  pinMode(IN3, OUTPUT);
  pinMode(IN4, OUTPUT);

  // LEDs
  pinMode(LED_HEARTBEAT, OUTPUT);
  pinMode(LED_INPUT, OUTPUT);

  // Initial states
  stopMotors();
  digitalWrite(LED_HEARTBEAT, LOW);
  digitalWrite(LED_INPUT, LOW);
}

void loop() {
  // === Bluetooth Connection LED Behavior ===
  if (SerialBT.hasClient()) {
    if (!hasShownConnectionBlink) {
      // Blink 5 times quickly when connected
      for (int i = 0; i < 5; i++) {
        digitalWrite(LED_HEARTBEAT, HIGH);
        delay(150);
        digitalWrite(LED_HEARTBEAT, LOW);
        delay(150);
      }
      hasShownConnectionBlink = true;
    }
  } else {
    hasShownConnectionBlink = false;
    // Blink LED every 1 second if not connected
    if (millis() - lastBlinkTime >= 1000) {
      heartbeatState = !heartbeatState;
      digitalWrite(LED_HEARTBEAT, heartbeatState);
      lastBlinkTime = millis();
    }
  }

  // === Bluetooth Command Handling ===
  if (SerialBT.available()) {
    char command = SerialBT.read();

    // Blink input LED when command is received
    digitalWrite(LED_INPUT, HIGH);
    delay(100);
    digitalWrite(LED_INPUT, LOW);

    // Handle motor command
    switch (command) {
      case '1': moveForward(); break;
      case '2': moveBackward(); break;
      case '3': turnLeft(); break;
      case '4': turnRight(); break;
      case '0': stopMotors(); break;
      default: stopMotors(); break;
    }
  }
}

// === Motor Control Functions ===

void moveForward() {
  digitalWrite(IN1, HIGH);
  digitalWrite(IN2, LOW);
  digitalWrite(IN3, HIGH);
  digitalWrite(IN4, LOW);
}

void moveBackward() {
  digitalWrite(IN1, LOW);
  digitalWrite(IN2, HIGH);
  digitalWrite(IN3, LOW);
  digitalWrite(IN4, HIGH);
}

void turnLeft() {
  digitalWrite(IN1, LOW);
  digitalWrite(IN2, HIGH);  // Left motor backward
  digitalWrite(IN3, HIGH);
  digitalWrite(IN4, LOW);   // Right motor forward
}

void turnRight() {
  digitalWrite(IN1, HIGH);
  digitalWrite(IN2, LOW);   // Left motor forward
  digitalWrite(IN3, LOW);
  digitalWrite(IN4, HIGH);  // Right motor backward
}

void stopMotors() {
  digitalWrite(IN1, LOW);
  digitalWrite(IN2, LOW);
  digitalWrite(IN3, LOW);
  digitalWrite(IN4, LOW);
}
