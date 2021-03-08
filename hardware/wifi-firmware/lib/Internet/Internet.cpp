#include "internet.h"

Internet::Internet() {}

bool Internet::connect(String ssid, String password)
{
    WiFi.mode(WIFI_OFF);
    delay(500);
    WiFi.mode(WIFI_STA);

    WiFi.begin(ssid, password);

    DEBUG_PRINT("Connecting");
    for (int rt = 0; rt < WIFI_WAIT_COUNT && WiFi.status() != WL_CONNECTED; rt++)
    {
        delay(500);
        DEBUG_PRINT(".");
    }
    if (WiFi.status() == WL_CONNECTED)
    {
        DEBUG_PRINT("Successfully connected!");
        DEBUG_PRINT(WiFi.localIP());
        if (client != NULL)
        {
            client = new BearSSL::WiFiClientSecure();
            client->setInsecure();
        }
    }
    return WiFi.status() == WL_CONNECTED;
}

String Internet::GET(String address)
{

    HTTPClient https;

    if (https.begin(*client, address))
    {
        DEBUG_PRINT("[GET] Initialized.");

        int httpCode = https.GET();

        if (httpCode > 0)
        {
            if (httpCode == HTTP_CODE_OK)
            {
                DEBUG_PRINT("[GET] Success!");
                String payload = https.getString();
                DEBUG_PRINT(payload);
                https.end();
                return payload;
            }
        }
        else
        {
            DEBUG_PRINT("[GET] Fail.");
        }

        https.end();
    }

    return ERROR_STR;
}

String Internet::POST(String address, String body)
{
    HTTPClient https;

    if (https.begin(*client, address))
    {
        DEBUG_PRINT("[POST] Initialized.");

        int httpCode = https.POST(body);

        if (httpCode > 0)
        {
            if (httpCode == HTTP_CODE_OK)
            {
                DEBUG_PRINT("[POST] Success!");
                String payload = https.getString();
                DEBUG_PRINT(payload);
                https.end();
                return payload;
            }
        }
        else
        {
            DEBUG_PRINT("[POST] Fail.");
        }

        https.end();
    }

    return ERROR_STR;
}
