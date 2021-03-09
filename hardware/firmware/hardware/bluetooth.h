#ifndef _BLUEOOTH_H_
#define _BLUEOOTH_H_

#include <string.h>
#include <stdio.h>
#include "../utils.h"

// TODO these are hardcoded, will need changing once we run on Linux
#define Bluetooth_ReceiverFifo (*(volatile unsigned char *)(0xFF210210))
#define Bluetooth_TransmitterFifo (*(volatile unsigned char *)(0xFF210210))
#define Bluetooth_InterruptEnableReg (*(volatile unsigned char *)(0xFF210212))
#define Bluetooth_InterruptIdentificationReg (*(volatile unsigned char *)(0xFF210214))
#define Bluetooth_FifoControlReg (*(volatile unsigned char *)(0xFF210214))
#define Bluetooth_LineControlReg (*(volatile unsigned char *)(0xFF210216))
#define Bluetooth_ModemControlReg (*(volatile unsigned char *)(0xFF210218))
#define Bluetooth_LineStatusReg (*(volatile unsigned char *)(0xFF21021A))
#define Bluetooth_ModemStatusReg (*(volatile unsigned char *)(0xFF21021C))
#define Bluetooth_ScratchReg (*(volatile unsigned char *)(0xFF21021E))
#define Bluetooth_DivisorLatchLSB (*(volatile unsigned char *)(0xFF210210))
#define Bluetooth_DivisorLatchMSB (*(volatile unsigned char *)(0xFF210212))
#define Bluetooth_ResetReg (*(volatile unsigned char *)(0xFF210220))

class Bluetooth
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
