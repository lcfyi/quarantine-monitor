#include "config.h"
#include <iostream>

ConfigController::ConfigController()
{
    // We won't bother with a destructor since we'll just let it leak
    ini_file = new mINI::INIFile(SETTINGS_CACHE_FILE);
    ini_data = new mINI::INIStructure();
    ini_file->read(*ini_data);
}

std::string ConfigController::get_value(std::string section, std::string key)
{
    // Safely read the data
    mtx.lock();
    std::string value = ini_data->get(section).get(key);
    mtx.unlock();
    return value;
}

void ConfigController::set_value(std::string section, std::string key, std::string value)
{
    mtx.lock();
    (*ini_data)[section][key] = value;
    ini_file->write(*ini_data);
    mtx.unlock();
}
