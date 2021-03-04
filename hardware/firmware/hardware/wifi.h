#ifndef _WIFI_H_
#define _WIFI_H_

#include <string.h>
#include <stdio.h>

// TODO these are hardcoded, will need changing once we run on Linux
#define WiFi_ReceiverFifo (*(volatile unsigned char *)(0xFF210200))
#define WiFi_TransmitterFifo (*(volatile unsigned char *)(0xFF210200))
#define WiFi_InterruptEnableReg (*(volatile unsigned char *)(0xFF210202))
#define WiFi_InterruptIdentificationReg (*(volatile unsigned char *)(0xFF210204))
#define WiFi_FifoControlReg (*(volatile unsigned char *)(0xFF210204))
#define WiFi_LineControlReg (*(volatile unsigned char *)(0xFF210206))
#define WiFi_ModemControlReg (*(volatile unsigned char *)(0xFF210208))
#define WiFi_LineStatusReg (*(volatile unsigned char *)(0xFF21020A))
#define WiFi_ModemStatusReg (*(volatile unsigned char *)(0xFF21020C))
#define WiFi_ScratchReg (*(volatile unsigned char *)(0xFF21020E))
#define WiFi_DivisorLatchLSB (*(volatile unsigned char *)(0xFF210200))
#define WiFi_DivisorLatchMSB (*(volatile unsigned char *)(0xFF210202))

class WiFi
{
public:
    void init();
    char put_char(char);
    char get_char();
    bool test_for_data();
    void flush();
    void write(char *);
};

#endif
