#include "wifi.h"

WiFi::WiFi() : UART(WiFi_LineControlReg, WiFi_DivisorLatchLSB, WiFi_DivisorLatchMSB, WiFi_FifoControlReg, WiFi_TransmitterFifo, WiFi_ReceiverFifo, WiFi_LineStatusReg)
{
    // In addition to the UART setup, we'll also reset the WiFi module
    init(WIFI_BAUD_RATE);
    ACCESS_ADDR(TRANSLATE_ADDR(WiFi_Reset)) = 0x0;
    usleep(500000);
    ACCESS_ADDR(TRANSLATE_ADDR(WiFi_Reset)) = 0x1;
    usleep(500000);
}

void WiFi::set_ssid(std::string ssid)
{
    write("0 " + ssid + "\n");
    wait_until_char('\n', TIMEOUT_CYCLES);
}

void WiFi::set_password(std::string password)
{
    write("1 " + password + "\n");
    wait_until_char('\n', TIMEOUT_CYCLES);
}

void WiFi::set_header(std::string key, std::string value)
{
    write("2 " + key + " " + value + "\n");
    wait_until_char('\n', TIMEOUT_CYCLES);
}

void WiFi::set_server_addr(std::string server_addr)
{
    write("3 " + server_addr + "\n");
    wait_until_char('\n', TIMEOUT_CYCLES);
}

void WiFi::connect()
{
    write("4\n");
    wait_until_char('\n', TIMEOUT_CYCLES);
}

std::string WiFi::GET()
{
    write("G\n");
    return read_until_char('\n');
}

std::string WiFi::POST(std::string body)
{
    write("P " + body + "\n");
    return read_until_char('\n');
}
