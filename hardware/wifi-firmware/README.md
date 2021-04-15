# ESP8266 WiFi Firmware

This subsection of the repository contains firmware for the ESP8266 that is built into the Terasic RFS Board. Responses from the server should be a single command without a newline at the end.

## Prerequisites

- VS Code
- Platform.io Extension

## Flashing the firmware

1. Connect 3.3V, GND, and UART_TX UART_RX to your FTDI/Arduino
2. Set the jumper to flash mode (juimp 2 and 3)
3. Flash

## UART Commands

`0 <ssid>\n` - set the wifi SSID

`1 <password>\n` - set the wifi password

`2 <header_key> <header_value>\n` - add a new header to be sent

`3 <addr>\n` - set the server address

`4\n` - connect to the previously configured wifi network

`G\n` - GET request to `<addr>`

`P <payload>\n` - POST request to `<addr>` with `<payload>` as its `text/plain` body

