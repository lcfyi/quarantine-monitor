#include <stdio.h>
#include <iostream>
#include <thread>
#include <string>
#include <mutex>

#include "utils/mmap.h"
#include "utils/config.h"
#include "utils/picosha2.h"
#include "hardware/wifi.h"
#include "hardware/bluetooth.h"
#include "hardware/io.h"
#include "hardware/accelerometer.h"

ConfigController config;
std::mutex wifi_init_mutex;
bool wifi_init = false;
bool wifi_success = false;
bool debug = false;

// We don't lock this one since it's set only by one thread,
// so it's okay if it gets missed by another
bool accelerometer_triggered = false;
bool face_verified = false;
bool bluetooth_triggered = false;

/**
 * Deal with the wrap-around values from the accelerometer.
 */
bool accelerometer_compare(int before, int after, int tolerance)
{
    return abs(before - after) > tolerance && abs(before - after + 65535) > tolerance && abs(before - after - 65535) > tolerance;
}

void accelerometer_thread()
{
    if (debug)
        std::cout << "[ACCEL] Thread starting." << std::endl;

    Accelerometer accelerometer;

    std::string at_setting = config.get_value(config.ACCELEROMETER_SETTINGS, "tolerance");

    int at = 10;

    if (at_setting.length())
    {
        at = std::stoi(at_setting);
    }

    if (debug)
        std::cout << "[ACCEL] Accelerometer value set at: " << at << std::endl;

    Accelerometer::Data last = accelerometer.get_data();

    while (1)
    {
        Accelerometer::Data curr = accelerometer.get_data();

        accelerometer_triggered = accelerometer_triggered || accelerometer_compare(curr.x, last.x, at) || accelerometer_compare(curr.y, last.y, at) || accelerometer_compare(curr.z, last.z, at);

        last = curr;
        usleep(10000); // 10ms
    }
}

void wifi_thread()
{
    if (debug)
        std::cout << "[WIFI] Thread starting." << std::endl;

    WiFi wifi;
    long counter = 0;
    std::string SERVER_OK = "OK";

    while (1)
    {
        if (debug)
            std::cout << "[WIFI] Loop." << std::endl;

        wifi_init_mutex.lock();
        if (!wifi_init)
        {
            wifi_success = false;
            if (debug)
                std::cout << "[WIFI] Initializing..." << std::endl;
            wifi.reset();
            wifi.set_ssid(config.get_value(config.WIFI_SETTINGS, "ssid"));
            wifi.set_password(config.get_value(config.WIFI_SETTINGS, "password"));
            wifi.set_server_addr(config.get_value(config.SERVER_SETTINGS, "addr"));
            wifi.set_header("Base", config.get_value(config.BASE_SETTINGS, "base"));
            wifi.set_header("Token", config.get_value(config.BASE_SETTINGS, "token"));
            if (debug)
                std::cout << "[WIFI] Connecting..." << std::endl;
            wifi.connect();
            wifi_init = true;
            if (debug)
                std::cout << "[WIFI] Testing connection" << std::endl;
            std::string v = wifi.GET();
            if (debug)
                std::cout << "[WIFI] Value from get: " << v << std::endl;
            if (v.rfind(SERVER_OK, 0) == 0)
            {
                if (debug)
                    std::cout << "[WIFI] OK" << std::endl;
                wifi_success = true;
            }
            counter = 0;
        }
        wifi_init_mutex.unlock();

        if (wifi_success)
        {
            std::string payload = "{\"h\":\"" + std::to_string(counter) + "\",\"s\":{\"a\":" + std::to_string(!accelerometer_triggered) + ",\"b\":" + std::to_string(!bluetooth_triggered);
            std::string checksum;
            if (face_verified)
            {
                if (debug)
                    std::cout << "[WIFI] Sending face verification flag" << std::endl;
                payload += ",\"f\": 1";
                face_verified = false;
            }
            payload += "}}";
            accelerometer_triggered = false;
            picosha2::hash256_hex_string(payload, checksum);
            counter++;
            while (true)
            {
                std::string resp = wifi.POST(payload + ";" + checksum);
                if (debug)
                    std::cout << "[WIFI] POST response: " << resp << std::endl;
                if (resp == SERVER_OK)
                    break;

                usleep(100000); // 100ms
            }
        }
        usleep(1000000); // 1000ms
    }
}

std::string bluetooth_command(std::string command)
{
    if (debug)
        std::cout << "[BLUETOOTH] command: " << command << std::endl;

    if (command.rfind("set-ssid", 0) == 0)
    { // Returns position of the found value
        std::string ssid = command.substr(9);
        config.set_value(config.WIFI_SETTINGS, "ssid", ssid);
    }
    else if (command.rfind("set-pass", 0) == 0)
    {
        std::string password = command.substr(9);
        config.set_value(config.WIFI_SETTINGS, "password", password);
    }
    else if (command.rfind("set-addr", 0) == 0)
    {
        std::string addr = command.substr(9);
        config.set_value(config.SERVER_SETTINGS, "addr", addr);
    }
    else if (command.rfind("ini-rset", 0) == 0)
    {
        if (wifi_init)
        {
            wifi_init_mutex.lock();
            wifi_init = false;
            wifi_init_mutex.unlock();
        }
    }
    else if (command.rfind("get-stat", 0) == 0)
    {
        return std::to_string(wifi_success) + "\n";
    }
    else if (command.rfind("set-face", 0) == 0)
    {
        face_verified = true;
    }

    return command;
}

void bluetooth_thread()
{
    if (debug)
        std::cout << "[BLUETOOTH] Thread starting." << std::endl;
    Bluetooth bluetooth;
    const int RETRY_COUNT = 3;
    const long CHALLENGE_INTERVAL = 10000;
    long counter = 0;

    while (1)
    {
        if (bluetooth.available())
        {
            if (debug)
                std::cout << "[BLUETOOTH] Data available." << std::endl;
            std::string read_value = "";
            for (int tries = 0; tries < RETRY_COUNT;)
            {
                char next_value = bluetooth.read();
                if (!next_value)
                {
                    tries++;
                }
                else if (next_value == '\n')
                {
                    std::string response = bluetooth_command(read_value);
                    if (response != "")
                    {
                        if (debug)
                            std::cout << "[BLUETOOTH] Sending response " << response << std::endl;
                        bluetooth.send(response);
                    }
                    break;
                }
                else
                {
                    read_value += next_value;
                }
            }
            counter = 0;
            bluetooth_triggered = false;
            bluetooth.bt_flush();
        }

        // We've gone through the interval without seeing any new packets, flag it
        if (counter > CHALLENGE_INTERVAL)
        {
            bluetooth_triggered = true;
        }
        else
        {
            counter++;
        }
        usleep(100); // We need to poll the bus often in order to clear the buffer
    }
}

int main()
{
    initialize_mmap();

    config.set_value(config.BASE_SETTINGS, "base", "123");
    config.set_value(config.BASE_SETTINGS, "token", "demo-token");
    std::string debug_setting = config.get_value(config.BASE_SETTINGS, "debug");

    if (debug_setting.length())
    {
        debug = std::stoi(debug_setting);
    }

    if (debug)
        printf("Virtual base: %p\n", virtual_base);

    std::thread wt(wifi_thread);
    std::thread bt(bluetooth_thread);
    std::thread at(accelerometer_thread);

    wt.join();
    bt.join();
    at.join();

    return 0;
}
