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
  Serial.setTimeout(SERIAL_TIMEOUT);
}

void loop()
{
  if (Serial.available())
  {
    String command = Serial.readStringUntil('\n');
    String res;
    switch (command[0])
    {
    case '0':
      // set SSID
      *ssid = command.substring(2);
      Serial.print("SSID set\n");
      break;
    case '1':
      // set password
      *password = command.substring(2);
      Serial.print("Password set\n");
      break;
    case '2':
      // set device ID
      *deviceId = command.substring(2);
      Serial.print("Device ID set\n");
      break;
    case '3':
      // Set base server address
      *serverAddr = command.substring(2);
      Serial.print("Server set\n");
      break;
    case '4':
      // Connect to WiFi
      res = internet.connect(*ssid, *password);
      Serial.print(res);
      Serial.print("\n");
      break;
    case '5':
      // TODO reconnect support
    case 'G':
      internet.GET(*serverAddr);
      Serial.print("GET\n");
      break;
    case 'P':
      internet.POST(*serverAddr, command.substring(2));
      Serial.print("POST ");
      Serial.print(command.substring(2));
      Serial.print("\n");
      break;
    }
  }
}