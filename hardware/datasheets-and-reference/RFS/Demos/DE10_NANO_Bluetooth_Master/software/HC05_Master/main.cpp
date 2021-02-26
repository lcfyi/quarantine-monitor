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
#include "CHC05.h"


const int MaxList = 9;


void SetConfig(bool bEnable){
	IOWR(MODE_CONTROL_BASE,0,bEnable);
}

int query_users(void){
    int nChoice = 0;
    scanf("%d", &nChoice);
    printf("%d\r\n", nChoice);
    return nChoice;
}


int main()
{
	CUart Uart;
 	Uart.Open(HC_05_UART_NAME);
 	CHC05 HC05;

 	char szData[45];
 	int nChoice = 0;
 	char *szDeviceList[MaxList];
 	for(int i=0;i<9;i++)
 		szDeviceList[i] = (char *)malloc(35*sizeof(char));

 	printf("HC-05 Master Demo\r\n");
 	SetConfig(true);

  	printf("AT Test Command\n");
  	HC05.SetupSend(Uart,"AT\r\n");

  	printf("INIT\n");
  	HC05.SetupSend(Uart,"AT+INIT\r\n");

  	printf("Set UART\n");
    HC05.SetupSend(Uart,"AT+UART=115200,0,0\r\n");

  	printf("Set to Master Role\n");
    HC05.SetupSend(Uart,"AT+ROLE=1\r\n");

  	printf("Inquire device\n");
  	HC05.SetInquireDevice(Uart,1,9,48);

    printf("Device List\n");
    HC05.DeviceList(Uart,"AT+INQ\r\n",szDeviceList);

    printf("GetName Device\n");
    for(int i=0;i<=8;i++){
      if(!strcmp(szDeviceList[i],"\0")){
       continue;
      }else{
    	printf("Device[%d]:%s\n",i,szDeviceList[i]);
        sprintf(szData,"AT+RNAME?%s\r\n",szDeviceList[i]);
        HC05.GetNameList(Uart,szData);
      }
    }

    printf("Please input you want to pair and link device\n");
    nChoice = query_users();
    sprintf(szData,"AT+PAIR=%s,%d\r\n",szDeviceList[nChoice],30);
    HC05.SetupSend(Uart,szData);

    usleep(1000000);
    sprintf(szData,"AT+BIND=%s\r\n",szDeviceList[nChoice]);
    HC05.SetupSend(Uart,szData);
    SetConfig(false);
    usleep(1000000);
 	for(int i=0;i<9;i++)
 		free(szDeviceList[i]);

    while(1){
      HC05.MessageSend(Uart,"The device's Bluetooth location is connected by the FPGA through the RFS module.\n");
      usleep(2000000);
    }
 	for(int i=0;i<9;i++)
 		free(szDeviceList[i]);
  return 0;
}

