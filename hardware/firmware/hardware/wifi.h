#ifndef _WIFI_H_
#define _WIFI_H_

#include <unistd.h>
#include <string>
#include "uart.h"
#include "../utils/mmap.h"

/**
 * Mapped based on QSys.
 */
#define WiFi_ReceiverFifo 0xFF210200
#define WiFi_TransmitterFifo 0xFF210200
#define WiFi_InterruptEnableReg 0xFF210202
#define WiFi_InterruptIdentificationReg 0xFF210204
#define WiFi_FifoControlReg 0xFF210204
#define WiFi_LineControlReg 0xFF210206
#define WiFi_ModemControlReg 0xFF210208
#define WiFi_LineStatusReg 0xFF21020A
#define WiFi_ModemStatusReg 0xFF21020C
#define WiFi_ScratchReg 0xFF21020E
#define WiFi_DivisorLatchLSB 0xFF210200
#define WiFi_DivisorLatchMSB 0xFF210202

/**
 * Special mapping for the reset pin.
 */
#define WiFi_Reset 0xFF200060

#define WIFI_BAUD_RATE 115200

class WiFi : public UART
{
public:
    WiFi();
    /**
     * The functions below assume a stateful WiFi module.
     */
    void set_ssid(std::string ssid);
    void set_password(std::string password);
    void set_header(std::string key, std::string value);
    void set_server_addr(std::string server_addr);
    void connect();
    void reset();

    std::string GET();
    std::string POST(std::string body);
};

#endif
