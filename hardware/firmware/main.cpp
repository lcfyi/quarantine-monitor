#include <stdio.h>
#include "hardware/wifi.h"
//
//// // ALL parallel IO ports created by QSYS have a 32 bit wide interface as far as the processor,
//// // that is, it reads and writes 32 bit data to the port, even though the
//// // port itself might only be configures as an 8 or 10 or even 4 bit wide port
//// //
//// // To access the port and read switches and writes to leds/hex displays etc, we set
//// // up "int pointer", i.e. pointers to 32 bit data
//// // to read/write data from/to the port and discard any upper bits we don't need.
//// // in the case of reading the switches and copying to leds/hex displays, it doesn't matter as
//// // they are all 8 bits so the upper unused bit don't matter,
//// // but push button port is 4 bit input port so reading with will give us 28 bits of leading 0's
//// // followed by the 4 bits corresponding to the push buttons in bits 0-3 of the data we read fom port
//
//// // the addresses below were defined by us when we created our system with Qys and assigned
//// // addresses to each of the ports we added (open up Qsys and check the Address Tab if you are uncertain)
//
#define SWITCHES (volatile unsigned int *)(0xFF200000)
#define PUSHBUTTONS (volatile unsigned int *)(0xFF200010)

#define LEDS (volatile unsigned int *)(0xFF200020)
#define HEX0_1 (volatile unsigned int *)(0xFF200030)
#define HEX2_3 (volatile unsigned int *)(0xFF200040)
#define HEX4_5 (volatile unsigned int *)(0xFF200050)

// For Bluetooth
// #define RS232_ReceiverFifo (*(volatile unsigned char *)(0xFF210210))
// #define RS232_TransmitterFifo (*(volatile unsigned char *)(0xFF210210))
// #define RS232_InterruptEnableReg (*(volatile unsigned char *)(0xFF210212))
// #define RS232_InterruptIdentificationReg (*(volatile unsigned char *)(0xFF210214))
// #define RS232_FifoControlReg (*(volatile unsigned char *)(0xFF210214))
// #define RS232_LineControlReg (*(volatile unsigned char *)(0xFF210216))
// #define RS232_ModemControlReg (*(volatile unsigned char *)(0xFF210218))
// #define RS232_LineStatusReg (*(volatile unsigned char *)(0xFF21021A))
// #define RS232_ModemStatusReg (*(volatile unsigned char *)(0xFF21021C))
// #define RS232_ScratchReg (*(volatile unsigned char *)(0xFF21021E))
// #define RS232_DivisorLatchLSB (*(volatile unsigned char *)(0xFF210210))
// #define RS232_DivisorLatchMSB (*(volatile unsigned char *)(0xFF210212))

WiFi wifi;

int main()
{
    int switches;
    printf("Initializing WiFi UART.\n");
    wifi.init();
    printf("Done initializing UART.\n");

    int a;

    while (1)
    {
        switches = *SWITCHES;
        printf("Switches %d\n", switches);
        switch (switches)
        {
        case 1:
            wifi.write("AT+CWLAP\r\n");
            a = wifi.get_char();
            wifi.flush();
            break;
        case 2:
        default:
            wifi.write("AT\r\n");
            a = wifi.get_char();
            printf("%c", a);
            wifi.flush();
        }
        *LEDS = switches;
        *HEX0_1 = switches;
        *HEX2_3 = switches;
        *HEX4_5 = switches;
    }
}
