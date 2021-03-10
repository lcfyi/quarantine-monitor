#include "bluetooth.h"

Bluetooth::Bluetooth() : UART(Bluetooth_LineControlReg, Bluetooth_DivisorLatchLSB, Bluetooth_DivisorLatchMSB, Bluetooth_FifoControlReg, Bluetooth_TransmitterFifo, Bluetooth_ReceiverFifo, Bluetooth_LineStatusReg)
{
    init(BLUETOOTH_BAUD_RATE);
}

void Bluetooth::send(std::string value)
{
    for (char const &c : value)
    {
        put_char(c);
    }
}

bool Bluetooth::available()
{
    return test_for_data();
}

char Bluetooth::read()
{
    return get_char();
}
