#include "internet.h"

Internet::Internet()
{
    headers = new HeaderNode("Content-Type", "text/plain");
    headers->next = new HeaderNode("Accept", "*/*");
}

String Internet::connect(String ssid, String password)
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
        if (client == NULL)
        {
            client = new BearSSL::WiFiClientSecure();
            client->setInsecure();
        }
        return WiFi.localIP().toString();
    }
    else
    {
        return ERROR_STR;
    }
}

void Internet::addHeader(String rawString)
{
    String key = "";
    String value = "";
    bool keyProcessed = false;
    for (unsigned i = 0; i < rawString.length(); i++)
    {
        if (rawString[i] == ' ' && !keyProcessed)
        {
            keyProcessed = true;
            continue;
        }
        if (rawString[i] != '\n')
        {
            if (!keyProcessed)
            {
                key += rawString[i];
            }
            else
            {
                value += rawString[i];
            }
        }
    }
    addHeader(key, value);
}

void Internet::addHeader(String key, String value)
{
    HeaderNode *tail = headers;

    while (tail->next != NULL)
    {
        tail = tail->next;
    }
    tail->next = new HeaderNode(key, value);
}

String Internet::GET(String address)
{

    HTTPClient https;
    int res;

    if (address.startsWith("https"))
    {
        res = https.begin(*client, address);
    }
    else
    {
        res = https.begin(address);
    }

    if (res)
    {
        DEBUG_PRINT("[GET] Initialized.");

        HeaderNode *ptr = headers;

        while (ptr != NULL)
        {
            https.addHeader(ptr->key, ptr->value);
            ptr = ptr->next;
        }

        int httpCode = https.GET();

        if (httpCode > 0)
        {
            if (httpCode == HTTP_CODE_OK)
            {
                DEBUG_PRINT("[GET] Success!");
                String payload = https.getString();
                DEBUG_PRINT(payload);
                https.end();
                if (payload.charAt(payload.length() - 1) != '\n')
                {
                    payload += '\n';
                }
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
    int res;

    if (address.startsWith("https"))
    {
        res = https.begin(*client, address);
    }
    else
    {
        res = https.begin(address);
    }

    if (res)
    {
        DEBUG_PRINT("[POST] Initialized, sending " + body);

        HeaderNode *ptr = headers;

        while (ptr != NULL)
        {
            https.addHeader(ptr->key, ptr->value);
            ptr = ptr->next;
        }

        int httpCode = https.POST(body);

        if (httpCode > 0)
        {
            if (httpCode == HTTP_CODE_OK)
            {
                DEBUG_PRINT("[POST] Success!");
                String payload = https.getString();
                DEBUG_PRINT(payload);
                https.end();
                if (payload.charAt(payload.length() - 1) != '\n')
                {
                    payload += '\n';
                }
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
