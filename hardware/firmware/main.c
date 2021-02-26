#include <stdio.h>
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

// For WiFi
#define RS232_ReceiverFifo (*(volatile unsigned char *)(0xFF210200))
#define RS232_TransmitterFifo (*(volatile unsigned char *)(0xFF210200))
#define RS232_InterruptEnableReg (*(volatile unsigned char *)(0xFF210202))
#define RS232_InterruptIdentificationReg (*(volatile unsigned char *)(0xFF210204))
#define RS232_FifoControlReg (*(volatile unsigned char *)(0xFF210204))
#define RS232_LineControlReg (*(volatile unsigned char *)(0xFF210206))
#define RS232_ModemControlReg (*(volatile unsigned char *)(0xFF210208))
#define RS232_LineStatusReg (*(volatile unsigned char *)(0xFF21020A))
#define RS232_ModemStatusReg (*(volatile unsigned char *)(0xFF21020C))
#define RS232_ScratchReg (*(volatile unsigned char *)(0xFF21020E))
#define RS232_DivisorLatchLSB (*(volatile unsigned char *)(0xFF210200))
#define RS232_DivisorLatchMSB (*(volatile unsigned char *)(0xFF210202))
#define RS232_ResetReg (*(volatile unsigned char *)(0xFF210220))

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

/**************************************************************************
/* Subroutine to initialise the RS232 Port by writing some data
** to the internal registers.
** Call this function at the start of the program before you attempt
** to read or write to data via the RS232 port
**
** Refer to UART data sheet for details of registers and programming
***************************************************************************/
void Init_RS232(void)
{
    // set bit 7 of Line Control Register to 1, to gain access to the baud rate registers
    RS232_LineControlReg = 0x80;
    // set Divisor latch (LSB and MSB) with correct value for required baud rate
    // baud rate divisor rate = (freq of BR_clk (50mhz)) / (desired baud rate * 16) ... 50000000/(115200*16) = 27 = 0x001b
    RS232_DivisorLatchLSB = 0x1b;
    RS232_DivisorLatchMSB = 0x00;
    // 38400, we NEED this for BT; TODO change it?
//	 RS232_DivisorLatchLSB = 0x51;
//	 RS232_DivisorLatchMSB = 0x00;
    // set bit 7 of Line control register back to 0 and
    RS232_LineControlReg = 0x00;
    // program other bits in that reg for 8 bit data, 1 stop bit, no parity etc
    RS232_LineControlReg = 0x03;
    // Reset the Fifo’s in the FiFo Control Reg by setting bits 1 & 2
    RS232_FifoControlReg = 0x06;
    // Now Clear all bits in the FiFo control registers
    RS232_FifoControlReg = 0x00;
    // Now reset the wifi chip
    RS232_ResetReg = 0x01;
    RS232_ResetReg = 0x00;
}

int putcharRS232(int c)
{
    // wait for Transmitter Holding Register bit (5) of line status register to be '1'
    while ((RS232_LineStatusReg & 0x20) != 0x20)
    {
    }

    // indicating we can write to the device
    // write character to Transmitter fifo register
    RS232_TransmitterFifo = c;

    // return the character we printed
    return c;
}
int getcharRS232(void)
{
    // wait for Data Ready bit (0) of line status register to be '1'
    //    while ((RS232_LineStatusReg & 0x1) != 0x01)
    //    {
    //    }
    while (!RS232TestForReceivedData())
    {
    }

    // read new character from ReceiverFiFo register
    // return new character
    return RS232_ReceiverFifo;
}
// the following function polls the UART to determine if any character
// has been received. It doesn't wait for one, or read it, it simply tests
// to see if one is available to read from the FIFO
int RS232TestForReceivedData(void)
{
    // if RS232_LineStatusReg bit 0 is set to 1
    //return TRUE, otherwise return FALSE
//    return 1;
    return ((RS232_LineStatusReg & 0x1) == 0x1);
}
//
// Remove/flush the UART receiver buffer by removing any unread characters
//
void RS232Flush(void)
{
    // while bit 0 of Line Status Register == ‘1’
    // read unwanted char out of fifo receiver buffer
    // return; // no more characters so return
    while (RS232TestForReceivedData())
    {
        int read = RS232_ReceiverFifo;
        printf("%c", read);
    }
}

void writeCharacters(char *strs, int length)
{
	for (int i = 0; i < length; i++) {
		putcharRS232((int)strs[i]);
	}
}

int main()
{
    int switches;
    printf("Initializing WiFi UART.\n");
    Init_RS232();
    printf("Done initializing UART.\n");

//    writeCharacters("AT\r\n", 4);
//    int a = getcharRS232();
//    printf("%c", a);
//    RS232Flush();
    writeCharacters("AT+CWMODE=1\r\n", 13);
    int a;
    do {
    	a = getcharRS232();
    	printf("%c", a);
    } while (a != 'K');
    RS232Flush();
    writeCharacters("AT+CWLAP\r\n", 10);
    a = getcharRS232();
    printf("%c", a);
    RS232Flush();


    while (1)
    {
//    	RS232_ResetReg = 0x01;
//		RS232_ResetReg = 0x00;
//		writeCharacters("AT+CWLAP\r\n", 10);
		int a = getcharRS232();
		printf("%c", a);
		RS232Flush();
//    	writeCharacters("AT\r\n", 4);
//    	a = getcharRS232();
//    	printf("%c", a);
//        RS232Flush();

        switches = *SWITCHES;
        *LEDS = switches;
        *HEX0_1 = switches;
        *HEX2_3 = switches;
        *HEX4_5 = switches;
    }
}
