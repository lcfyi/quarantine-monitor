#ifndef _GLOBAL_H_
#define _GLOBAL_H_

#define DEBUG 1
#define BUFFER_SIZE 4096
#define SERIAL_BAUD 115200

#define WIFI_WAIT_COUNT 60

#ifdef DEBUG
#define DEBUG_PRINT(x) Serial.println(x)
#else
#define DEBUG_PRINT(x)
#endif

#endif