#include <stdio.h>
#include <iostream>
#include <thread>

#include "mmap.h"
#include "hardware/wifi.h"
#include "hardware/bluetooth.h"
#include "hardware/io.h"
#include "hardware/accelerometer.h"

void wifi_thread()
{
	WiFi wifi;
}

void bluetooth_thread()
{
	Bluetooth bluetooth;
}

void accelerometer_thread()
{
	Accelerometer accelerometer;
}

int main()
{
	initialize_mmap();

	Bluetooth bluetooth;
	Accelerometer accel;

	printf("Virtual base: %p\n", virtual_base);

	while (1)
	{
		Accelerometer::Data data = accel.get_data();
		printf("%d %d %d\n", data.x, data.y, data.z);
		bluetooth.send("x: " + std::to_string(data.x) + " y: " + std::to_string(data.y) + " z: " + std::to_string(data.z) + "\n");
		usleep(1000000);
	}
}
