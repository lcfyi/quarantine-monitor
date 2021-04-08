#ifndef _BLUEOOTH_H_
#define _BLUEOOTH_H_

#include "uart.h"

#define Bluetooth_ReceiverFifo 0xFF210210
#define Bluetooth_TransmitterFifo 0xFF210210
#define Bluetooth_InterruptEnableReg 0xFF210212
#define Bluetooth_InterruptIdentificationReg 0xFF210214
#define Bluetooth_FifoControlReg 0xFF210214
#define Bluetooth_LineControlReg 0xFF210216
#define Bluetooth_ModemControlReg 0xFF210218
#define Bluetooth_LineStatusReg 0xFF21021A
#define Bluetooth_ModemStatusReg 0xFF21021C
#define Bluetooth_ScratchReg 0xFF21021E
#define Bluetooth_DivisorLatchLSB 0xFF210210
#define Bluetooth_DivisorLatchMSB 0xFF210212
#define Bluetooth_ResetReg 0xFF210220

#define BLUETOOTH_BAUD_RATE 115200

class Bluetooth : public UART
{
public:
    Bluetooth();

    void send(std::string value);
    bool available();
    char read();
    void bt_flush();
};

#endif
