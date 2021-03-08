#include "wifi.h"

void WiFi::init()
{
    // set bit 7 of Line Control Register to 1, to gain access to the baud rate registers
    WiFi_LineControlReg = 0x80;
    // set Divisor latch (LSB and MSB) with correct value for required baud rate
    // baud rate divisor rate = (freq of BR_clk (50mhz)) / (desired baud rate * 16) ... 50000000/(115200*16) = 27 = 0x001b
    WiFi_DivisorLatchLSB = 0x1b;
    WiFi_DivisorLatchMSB = 0x00;
    // set bit 7 of Line control register back to 0 and
    WiFi_LineControlReg = 0x00;
    // program other bits in that reg for 8 bit data, 1 stop bit, no parity etc
    WiFi_LineControlReg = 0x03;
    // Reset the Fifo's in the FiFo Control Reg by setting bits 1 & 2
    WiFi_FifoControlReg = 0x06;
    // Now Clear all bits in the FiFo control registers
    WiFi_FifoControlReg = 0x00;
    // Reset the wifi chip (it's active low)
    WiFi_Reset = 0x00;
    naiveSleep(100);
    WiFi_Reset = 0x01;
    naiveSleep(1000);
    flush();
}

char WiFi::put_char(char c)
{
    while ((WiFi_LineStatusReg & 0x20) != 0x20)
        ;

    WiFi_TransmitterFifo = c;

    return c;
}

char WiFi::get_char()
{
    while (!this->test_for_data())
        ;

    return WiFi_ReceiverFifo;
}

bool WiFi::test_for_data()
{
    return ((WiFi_LineStatusReg & 0x1) == 0x1);
}

void WiFi::flush()
{
    while (this->test_for_data())
    {
        int read = WiFi_ReceiverFifo;
        printf("%c", read);
    }
}

void WiFi::write(char *str)
{
    int len = strlen(str);

    for (int i = 0; i < len; i++)
    {
        this->put_char(str[i]);
    }
}
