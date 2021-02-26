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

#include "terasic_includes.h"
#include "light_sensor.h"
#include "rh_temp.h"
#include "mpu9250.h"


void Sensor_Report(void){
  	bool bPass;

  	////////////////////////////////
  	// report light sensor
  	alt_u16 light0 = 0, light1 = 0;
  	bPass = Get_light(&light0, &light1);
  	if(bPass){
  		printf("light0 = %d, light1 = %d\r\n", light0, light1);
  	}
  	else{
  		printf("get light0 and light1 values failed!\r\n");
  	}

  	////////////////////////////////
  	// report HDC1000 temperature & humidity sensor
  	float fTemperature, fHumidity;
  	bPass = RH_Temp_Sensor_Read(&fTemperature, &fHumidity);
  	if (bPass){
		printf("Temperature: %.3f*C\r\n", fTemperature);
    	printf("Humidity: %.3f%%\r\n",fHumidity);
  	}else{
  		printf("Failed to ready Temperature/Humidity sensor!\r\n");
  	}


  	////////////////////////////////
  	// report mpu9250 9-axis sensor
  	float ax, ay, az;
  	float gx, gy, gz;
  	float mx, my, mz;
  	getMotion9(&ax, &ay, &az, &gx, &gy, &gz, &mx, &my, &mz);
  	printf("9-axis info:\r\n");
  	printf("ax = %.3f, ay = %.3f, az = %.3f\r\n", ax, ay, az);
  	printf("gx = %.3f, gy = %.3f, gz = %.3f\r\n", gx, gy, gz);
  	printf("mx = %.3f, my = %.3f, mz = %.3f\r\n", mx, my, mz);

  	printf("\r\n");
}

int main()
{
	printf("Welcome to DE10_NANO RFS_SENSOR DEMO!\r\n");

  	bool bPass = FALSE;

  	////////////////////////////////////
  	// init light sensor i2c and power on
  	Light_Init(LIGHT_I2C_OPENCORES_BASE);
  	bPass = Light_PowerSwitch(TRUE);
  	if(bPass)
  		printf("light sensor power up successful!\r\n");
  	else
  		printf("light sensor power up failed!\r\n");

  	////////////////////////////////////
  	// init HDC1000: temperature and humidity sensor
  	RH_Temp_Init(RH_TEMP_I2C_OPENCORES_BASE);
  	bPass = RH_Temp_Sensor_Init();
  	if(bPass)
  		printf("Init HDC1000 successful!\r\n");
  	else
  		printf("Init HDC1000 failed!\r\n");



  	////////////////////////////////////
  	//init MPU9250 9-axis sensor
  	MPU9250_Init(MPU_I2C_OPENCORES_BASE);
  	MPU9250_initialize();
  	printf("\r\n");


  	while(1){ // report every second
  		Sensor_Report();
  		usleep(1000*1000);

  	}



  return 0;
}
