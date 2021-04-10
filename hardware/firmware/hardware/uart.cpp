#include "uart.h"

#define SLEEP_TIME 0

std::mutex bus_mutex;

char get_addr(char *addr)
{
    bus_mutex.lock();
    char c = (*((volatile unsigned char *)(addr)));
    bus_mutex.unlock();
    return c;
}

void set_addr(char *addr, char value)
{
    bus_mutex.lock();
    (*((volatile unsigned char *)(addr))) = value;
    bus_mutex.unlock();
}

UART::UART(int line_control_reg,
           int divisor_latch_lsb,
           int divisor_latch_msb,
           int fifo_control_reg,
           int transmitter_fifo,
           int receiver_fifo,
           int line_status_reg)
{
    _line_control_reg = line_control_reg;
    _divisor_latch_lsb = divisor_latch_lsb;
    _divisor_latch_msb = divisor_latch_msb;
    _fifo_control_reg = fifo_control_reg;
    _transmitter_fifo = transmitter_fifo;
    _receiver_fifo = receiver_fifo;
    _line_status_reg = line_status_reg;
}

void UART::init(int baud_rate)
{
    // We assume the mmap is defined properly here
    // Gain access to the baud rate registers
    set_addr(TRANSLATE_ADDR(_line_control_reg), 0x80);

    int divisor_latch_rate = 50000000 / (baud_rate * 16);
    set_addr(TRANSLATE_ADDR(_divisor_latch_lsb), divisor_latch_rate & 0xff);
    set_addr(TRANSLATE_ADDR(_divisor_latch_msb), (divisor_latch_rate >> 8) & 0xff);

    // Set bit 7 of line control register back to 0
    set_addr(TRANSLATE_ADDR(_line_control_reg), 0x00);

    // Program other bits in that reg for 8 bit data, 1 stop bit, no parity
    set_addr(TRANSLATE_ADDR(_line_control_reg), 0x03);

    // Reset the Fifos in the FiFo control reg by settings bits 1 & 2
    set_addr(TRANSLATE_ADDR(_fifo_control_reg), 0x06);

    // Clear all bits in the FiFo control regs
    set_addr(TRANSLATE_ADDR(_fifo_control_reg), 0x00);
}

char UART::put_char(char c)
{
    while (((get_addr(TRANSLATE_ADDR(_line_status_reg))) & 0x20) != 0x20)
    {
        usleep(SLEEP_TIME);
    }

    set_addr(TRANSLATE_ADDR(_transmitter_fifo), c);

    return c;
}

char UART::get_char(long timeout)
{
    long cycles = 0;
    for (; !test_for_data() && cycles < timeout; cycles++)
    {
        usleep(SLEEP_TIME);
    }

    if (cycles == timeout)
    {
        return 0;
    }
    else
    {
        return get_addr(TRANSLATE_ADDR(_receiver_fifo));
    }
}

bool UART::test_for_data()
{
    return (get_addr(TRANSLATE_ADDR(_line_status_reg)) & 0x1) == 0x1;
}

void UART::flush(bool print_to_stdout)
{
    while (test_for_data())
    {
        usleep(SLEEP_TIME);
        char read = get_addr(TRANSLATE_ADDR(_receiver_fifo));
        if (print_to_stdout)
        {
            printf("%c", read);
        }
    }
}

void UART::write(std::string str)
{
    for (char const &c : str)
    {
        usleep(SLEEP_TIME);
        put_char(c);
    }
}

void UART::wait_until_char(char c, long timeout_cycles)
{
    char read;
    do
    {
        usleep(SLEEP_TIME);
        read = get_char(timeout_cycles);
    } while (read != c);
}

/**
 * This reads up until char c, but does not include char c.
 */
std::string UART::read_until_char(char c)
{
    std::string read = "";
    char read_char;
    const int RETRY_COUNT = 3;
    int tries = 0;
    do
    {
        usleep(SLEEP_TIME);
        read_char = get_char();
        if (!read_char)
        {
            tries++;
        }
        else if (read_char != c)
        {
            read += read_char;
        }
    } while (read_char != c && tries < RETRY_COUNT);
    return read;
}