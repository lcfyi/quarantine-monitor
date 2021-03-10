# ESP8266 WiFi Firmware

This subsection of the repository contains firmware for the ESP8266 that is built into the Terasic RFS Board. Responses from the server should be a single command without a newline at the end.

## Prerequisites

- VS Code
- Platform.io Extension

## Flashing the firmware

1. Connect 3.3V, GND, and UART_TX UART_RX to your FTDI/Arduino
2. Set the jumper to flash mode (juimp 2 and 3)
3. Flash