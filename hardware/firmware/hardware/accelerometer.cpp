#include "accelerometer.h"

Accelerometer::Accelerometer()
{
    // Set up i2c
    bool success = _init_i2c();
    if (success)
    {
        success = _init_adxl345();
    }

    if (!success)
    {
        exit(1);
    }
}

Accelerometer::Data Accelerometer::get_data()
{
    uint8_t sz_data[6];

    Accelerometer::Data data;

    while (!_is_data_ready())
        ;

    bool success = _reg_multi_read(ADXL345_REG_DATAX0, (uint8_t *)&sz_data, sizeof(sz_data));

    if (success)
    {
        data.x = ((sz_data[1] << 8) | sz_data[0]);
        data.y = ((sz_data[3] << 8) | sz_data[2]);
        data.z = ((sz_data[5] << 8) | sz_data[4]);
    }

    return data;
}

bool Accelerometer::_reg_write(uint8_t addr, uint8_t value)
{
    bool success = false;
    uint8_t sz_value[2];

    // Write to define register
    sz_value[0] = addr;
    sz_value[1] = value;

    if (write(_fd, &sz_value, sizeof(sz_value)) == sizeof(sz_value))
    {
        success = true;
    }

    return success;
}

uint8_t Accelerometer::_reg_read(uint8_t addr)
{
    uint8_t value;

    // Write to define register
    if (write(_fd, &addr, sizeof(addr)) == sizeof(addr))
    {
        // Read back value
        if (read(_fd, &value, sizeof(value)) == sizeof(value))
        {
            return value;
        }
    }
    return -1;
}

bool Accelerometer::_reg_multi_read(uint8_t readaddr, uint8_t readdata[], uint8_t len)
{
    bool success = false;

    // Write to define register
    if (write(_fd, &readaddr, sizeof(readaddr)) == sizeof(readaddr))
    {
        // Read back value
        if (read(_fd, readdata, len) == len)
        {
            success = true;
        }
    }

    return success;
}

bool Accelerometer::_init_i2c()
{
    if ((_fd = open(I2C_FILENAME, O_RDWR)) < 0)
    {
        printf("ERROR: Failed to open i2c bus.\n");
        exit(1);
    }

    if (ioctl(_fd, I2C_SLAVE, ADXL345_ADDR) < 0)
    {
        printf("ERROR: Failed to talk to slave.\n");
        exit(1);
    }

    return true;
}

bool Accelerometer::_init_adxl345()
{
    bool success = false;

    success = _reg_write(ADXL345_REG_DATA_FORMAT, XL345_RANGE_2G | XL345_FULL_RESOLUTION);
    // Output Data Rate: 50Hz
    if (success)
    {
        success = _reg_write(ADXL345_REG_BW_RATE, XL345_RATE_50); // 50 HZ
    }

    // INT_Enable: Data Ready
    if (success)
    {
        success = _reg_write(ADXL345_REG_INT_ENALBE, XL345_DATAREADY);
    }

    // Stop measure
    if (success)
    {
        success = _reg_write(ADXL345_REG_POWER_CTL, XL345_STANDBY);
    }

    // Start measure
    if (success)
    {
        success = _reg_write(ADXL345_REG_POWER_CTL, XL345_MEASURE);
    }

    return success;
}

bool Accelerometer::_is_data_ready()
{
    bool ready = false;
    uint8_t data8 = _reg_read(ADXL345_REG_INT_SOURCE);

    if (data8)
    {
        if (data8 & XL345_DATAREADY)
        {
            ready = true;
        }
    }

    return ready;
}