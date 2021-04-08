#ifndef _CONFIG_H_
#define _CONFIG_H_

#include "../utils/ini.h"
#include <string>
#include <mutex>

class ConfigController
{
public:
    ConfigController();
    std::string get_value(std::string section, std::string key);
    void set_value(std::string section, std::string key, std::string value);

    std::string SETTINGS_CACHE_FILE = "/home/root/settings.ini";
    std::string WIFI_SETTINGS = "wifi";
    std::string ACCELEROMETER_SETTINGS = "accel";
    std::string SERVER_SETTINGS = "server";
    std::string BASE_SETTINGS = "base";

private:
    mINI::INIFile *ini_file;
    mINI::INIStructure *ini_data;
    std::mutex mtx;
};

#endif