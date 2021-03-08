#ifndef _GLOBAL_H_
#define _GLOBAL_H_

#define DEBUG 0
#define BUFFER_SIZE 4096
#define SERIAL_BAUD 115200
#define SERIAL_TIMEOUT 1000

#define WIFI_WAIT_COUNT 60

#if DEBUG
#define DEBUG_PRINT(x) Serial.println(x)
#else
#define DEBUG_PRINT(x)
#endif

#endif