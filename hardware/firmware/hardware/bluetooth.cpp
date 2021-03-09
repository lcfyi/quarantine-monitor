#include "bluetooth.h"

void Bluetooth::init()
{
    // set bit 7 of Line Control Register to 1, to gain access to the baud rate registers
    Bluetooth_LineControlReg = 0x80;
    // set Divisor latch (LSB and MSB) with correct value for required baud rate
    // baud rate divisor rate = (freq of BR_clk (50mhz)) / (desired baud rate * 16) ... 81 = 51
    // baud rate for AT mode = 51
    // baud rate for connection mode = 1B
    // 50,000,000/(baudrate * 16)
    // test baud rate = 27 = 1B
    // test baud rate = 9600 = 45
    // 38400, we NEED this for BT; TODO change it?
    Bluetooth_DivisorLatchLSB = 0x1B;
    Bluetooth_DivisorLatchMSB = 0x00;
    // set bit 7 of Line control register back to 0 and
    Bluetooth_LineControlReg = 0x00;
    // program other bits in that reg for 8 bit data, 1 stop bit, no parity etc
    Bluetooth_LineControlReg = 0x03;
    // Reset the Fifoâ€™s in the FiFo Control Reg by setting bits 1 & 2
    Bluetooth_FifoControlReg = 0x06;
    // Now Clear all bits in the FiFo control registers
    Bluetooth_FifoControlReg = 0x00;
    // Now reset the wifi chip
    Bluetooth_ResetReg = 0x01;
    Bluetooth_ResetReg = 0x00;
}

char Bluetooth::put_char(char c)
{
    while ((Bluetooth_LineStatusReg & 0x20) != 0x20);

    Bluetooth_TransmitterFifo = c;

    return c;
}

char Bluetooth::get_char()
{
    while (!this->test_for_data());

    return Bluetooth_ReceiverFifo;
}

bool Bluetooth::test_for_data()
{
    return ((Bluetooth_LineStatusReg & 0x1) == 0x1);
}

void Bluetooth::flush()
{
    while (this->test_for_data())
    {
        int read = Bluetooth_ReceiverFifo;
        printf("%c", read);
    }
}

void Bluetooth::write(char *str)
{
    int len = strlen(str);

    for (int i = 0; i < len; i++)
    {
        this->put_char(str[i]);
    }
}
