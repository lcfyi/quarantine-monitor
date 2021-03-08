#include <Arduino.h>
#include <Internet.h>
#include <global.h>

String *ssid;
String *password;
String *serverAddr;
String *deviceId;
Internet internet;

void setup()
{
  ssid = new String("");
  password = new String("");
  serverAddr = new String("");
  deviceId = new String("");
  Serial.begin(SERIAL_BAUD);
  Serial.setTimeout(1000); // Default value
}

void loop()
{
  if (Serial.available())
  {
    String command = Serial.readStringUntil('\n');
    bool res;
    switch (command[0])
    {
    case '0':
      // set SSID
      *ssid = command.substring(2);
      Serial.print("K\n");
      break;
    case '1':
      // set password
      *password = command.substring(2);
      Serial.print("K\n");
      break;
    case '2':
      // set device ID
      *deviceId = command.substring(2);
      Serial.print("K\n");
      break;
    case '3':
      // Set base server address
      *serverAddr = command.substring(2);
      Serial.print("K\n");
    case '4':
      // Connect to WiFi
      res = internet.connect(*ssid, *password);
      if (res)
      {
        Serial.print("K\n");
      }
      else
      {
        Serial.print("!\n");
      }
      break;
    case '5':
      // TODO reconnect support
    case 'G':
      internet.GET(*serverAddr);
      break;
    case 'P':
      internet.POST(*serverAddr, command.substring(2));
      break;
    }
  }
}