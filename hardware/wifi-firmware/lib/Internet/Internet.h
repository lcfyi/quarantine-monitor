#ifndef _INTERNET_H_
#define _INTERNET_H_

#include <ESP8266Wifi.h>
#include <ESP8266HTTPClient.h>
#include <WiFiClientSecureBearSSL.h>
#include <Arduino.h>
#include "../global/global.h"

#define ERROR_STR "ERROR\n"
class HeaderNode
{
public:
    HeaderNode(String k, String v)
    {
        key = k;
        value = v;
    }
    String key;
    String value;
    HeaderNode *next = NULL;
};
class Internet
{
public:
    Internet();
    String connect(String ssid, String password);
    void addHeader(String rawString);
    void addHeader(String key, String value);
    String GET(String address);
    String POST(String address, String body);

private:
    bool _initialized = false;
    BearSSL::WiFiClientSecure *client;
    HeaderNode *headers;
};

#endif