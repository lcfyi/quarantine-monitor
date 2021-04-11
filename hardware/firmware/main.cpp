#include <stdio.h>
#include <iostream>
#include <thread>
#include <string>

#include "utils/mmap.h"
#include "utils/config.h"
#include "utils/picosha2.h"
#include "hardware/wifi.h"
#include "hardware/bluetooth.h"
#include "hardware/io.h"
#include "hardware/accelerometer.h"

ConfigController config;
bool wifi_init = false;
bool wifi_success = false;
bool debug = false;

// We don't lock this one since it's set only by one thread,
// so it's okay if it gets missed by another
bool accelerometer_triggered = false;
bool face_verified = false;
bool bluetooth_triggered = false;
bool accel_changed = true;
bool wifi_interval_changed = true;

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

    int at = 10;

    Accelerometer::Data last = accelerometer.get_data();

    while (1)
    {
        Accelerometer::Data curr = accelerometer.get_data();
        if (accel_changed)
        {
            std::string at_setting = config.get_value(config.ACCELEROMETER_SETTINGS, "tolerance");
            if (at_setting.length())
            {
                at = std::stoi(at_setting);
            }
            if (debug)
                std::cout << "[ACCEL] Accelerometer value set at: " << at << std::endl;
            accel_changed = false;
        }

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

    int interval = 1;
    long counter = 0;
    std::string SERVER_OK = "OK";

    while (1)
    {
        if (debug)
            std::cout << "[WIFI] Loop." << std::endl;

        if (!wifi_init)
        {
            wifi_init = true;
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
            while (true && wifi_init)
            {
                std::string resp = wifi.POST(payload + ";" + checksum);
                if (debug)
                    std::cout << "[WIFI] POST response: " << resp << std::endl;
                if (resp == SERVER_OK)
                    break;

                usleep(100000); // 100ms
            }
        }

        if (wifi_interval_changed)
        {
            std::string interval_setting = config.get_value(config.WIFI_SETTINGS, "interval");

            if (interval_setting.length())
            {
                interval = std::stoi(interval_setting);
            }

            if (debug)
                std::cout << "[WIFI] Interval set at: " << interval << std::endl;
            wifi_interval_changed = false;
        }

        usleep(1000000 * interval); // 1000ms
    }
}

std::string bluetooth_command(std::string command)
{
    if (debug)
        std::cout << "[BLUETOOTH] command: " << command << std::endl;

    if (command.rfind("set-ssid", 0) == 0)
    { // Returns position of the found value
        if (command.length() < 9)
        {
            return "Invalid command parameters!\n";
        }
        std::string ssid = command.substr(9);
        config.set_value(config.WIFI_SETTINGS, "ssid", ssid);
        return "Successfully set ssid to: " + ssid + "\n";
    }
    else if (command.rfind("set-pass", 0) == 0)
    {
        if (command.length() < 9)
        {
            return "Invalid command parameters!\n";
        }
        std::string password = command.substr(9);
        config.set_value(config.WIFI_SETTINGS, "password", password);
        return "Successfully set wifi password to: " + password + "\n";
    }
    else if (command.rfind("set-addr", 0) == 0)
    {
        if (command.length() < 9)
        {
            return "Invalid command parameters!\n";
        }
        std::string addr = command.substr(9);
        config.set_value(config.SERVER_SETTINGS, "addr", addr);
        return "Sucessfully set server address to: " + addr + "\n";
    }
    else if (command.rfind("ini-rset", 0) == 0)
    {
        wifi_init = false;
    }
    else if (command.rfind("get-stat", 0) == 0)
    {
        return std::to_string(wifi_success) + "\n";
    }
    else if (command.rfind("set-face", 0) == 0)
    {
        face_verified = true;
    }
    else if (command.rfind("set-debug", 0) == 0)
    {
        if (command.length() < 10)
        {
            return "Invalid command parameters!\n";
        }
        std::string value = command.substr(10);
        if (value == "1")
        {
            config.set_value(config.BASE_SETTINGS, "debug", value);
            debug = 1;
        }
        else if (value == "0")
        {
            config.set_value(config.BASE_SETTINGS, "debug", value);
            debug = 0;
        }
        else
        {
            return "Invalid debug value: should be 0 or 1.";
        }
        return "Set debug value to: " + value + "\n";
    }
    else if (command.rfind("set-base", 0) == 0)
    {
        if (command.length() < 9)
        {
            return "Invalid command parameters!\n";
        }
        std::string base = command.substr(9);
        config.set_value(config.BASE_SETTINGS, "base", base);
        return "Successfully set base ID to: " + base + "\n";
    }
    else if (command.rfind("set-token", 0) == 0)
    {
        if (command.length() < 10)
        {
            return "Invalid command parameters!\n";
        }
        std::string token = command.substr(10);
        config.set_value(config.BASE_SETTINGS, "token", token);

        return "Successfully set base token to: " + token + "\n";
    }
    else if (command.rfind("set-accel", 0) == 0)
    {
        if (command.length() < 10)
        {
            return "Invalid command parameters!\n";
        }
        std::string tolerance = command.substr(10);
        for (char const &c : tolerance)
        {
            if (!std::isdigit(c))
            {
                return "Not a valid number!\n";
            }
        }
        if (!tolerance.length())
        {
            return "Not a valid number!\n";
        }
        config.set_value(config.ACCELEROMETER_SETTINGS, "tolerance", tolerance);
        accel_changed = true;
        return "Successfully set accelerometer tolerance to: " + tolerance + "\n";
    }
    else if (command.rfind("set-inter", 0) == 0)
    {
        if (command.length() < 10)
        {
            return "Invalid command parameters!\n";
        }
        std::string interval = command.substr(10);
        for (char const &c : interval)
        {
            if (!std::isdigit(c))
            {
                return "Not a valid number!\n";
            }
        }
        if (!interval.length())
        {
            return "Not a valid number!\n";
        }
        config.set_value(config.WIFI_SETTINGS, "interval", interval);
        wifi_interval_changed = true;
        return "Successfully set wifi interval to: " + interval + "\n";
    }

    return command;
}

void bluetooth_thread()
{
    if (debug)
        std::cout << "[BLUETOOTH] Thread starting." << std::endl;
    Bluetooth bluetooth;
    const int RETRY_COUNT = 3;
    const long CHALLENGE_INTERVAL = 150000;
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
            if (debug)
                std::cout << "[BLUETOOTH] Flushed." << std::endl;
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
        usleep(10); // We need to poll the bus often in order to clear the buffer
    }
}

int main()
{
    initialize_mmap();

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
