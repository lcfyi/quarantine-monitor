/*
 * "Hello World" example.
 *
 * This example prints 'Hello from Nios II' to the STDOUT stream. It runs on
 * the Nios II 'standard', 'full_featured', 'fast', and 'low_cost' example
 * designs. It runs with or without the MicroC/OS-II RTOS and requires a STDOUT
 * device in your system's hardware.
 * The memory footprint of this hosted application is ~69 kbytes by default
 * using the standard reference design.
 *
 * For a reduced footprint version of this template, and an explanation of how
 * to reduce the memory footprint for a given application, see the
 * "small_hello_world" template.
 *
 */

#include <stdio.h>
#include "terasic_includes.h"
#include "CUart.h"



bool CommandRead(CUart   &Uart, char *pBuffer, int nBufferSize, int *prx_lenmand){
	int RxLen;
	char Data;
	bool bDone = false;
	int Len = 0;
	alt_u32 timeout;

	if (!Uart.Read((char *)&Data, sizeof(Data), &RxLen) || RxLen == 0)
			return false;

	*(pBuffer + 0) = Data;

	// read until find '\r'
	timeout = alt_nticks() + alt_ticks_per_second()/1000;
	Len = 1;
	while(!bDone){
		if (Uart.Read((char *)&Data, sizeof(Data), &RxLen) && RxLen == sizeof(Data)){
			*(pBuffer + Len) = Data;
			Len++;
			if (Data == '\n')
				bDone = true;
		}else if (Len >= nBufferSize){
			// buffer full
			bDone = true;
		}else if (alt_nticks() > timeout){
			// timeout
			bDone = true;
		}else{
		}
		usleep(10);

	}
	*prx_lenmand = Len;
	return true;
}


int main()
{
  CUart Uart;
  Uart.Open(HC_05_UART_NAME);
  int nReadLen;
  int Command, Param;
  char szData[30];
  printf("Press KEY1 to Config HC-05\r\n");

  while(1)
  {
	 if(!IORD(PIO_KEY_BASE,0))
	 {
       Uart.WriteString("AT\r\n");
       if(CommandRead(Uart, szData, sizeof(szData), &nReadLen))
       {
	      szData[nReadLen] = 0;
	      printf("AT Command \n");
	      printf("HC05 Response:%s\r\n",szData);
       }
       usleep(1000000);

       Uart.WriteString("AT+ORGL\r\n");
       if(CommandRead(Uart, szData, sizeof(szData), &nReadLen))
       {
	      szData[nReadLen] = 0;
	      printf("Set ORGL \n");
	      printf("HC05 Response:%s\r\n",szData);
       }
       usleep(1000000);

       Uart.WriteString("AT+UART=115200,0,0\r\n");

       if(CommandRead(Uart, szData, sizeof(szData), &nReadLen))
       {
	      szData[nReadLen] = 0;
	      printf("Set Baut Rate \n");
	      printf("HC05 Response:%s\r\n",szData);
       }
       usleep(1000000);
	 }


  }


  return 0;
}
