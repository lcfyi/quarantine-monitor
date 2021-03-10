#include "uart.h"

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
    ACCESS_ADDR(TRANSLATE_ADDR(_line_control_reg)) = 0x80;

    int divisor_latch_rate = 50000000 / (baud_rate * 16);
    ACCESS_ADDR(TRANSLATE_ADDR(_divisor_latch_lsb)) = divisor_latch_rate & 0xff;
    ACCESS_ADDR(TRANSLATE_ADDR(_divisor_latch_msb)) = (divisor_latch_rate >> 8) & 0xff;

    // Set bit 7 of line control register back to 0
    ACCESS_ADDR(TRANSLATE_ADDR(_line_control_reg)) = 0x00;

    // Program other bits in that reg for 8 bit data, 1 stop bit, no parity
    ACCESS_ADDR(TRANSLATE_ADDR(_line_control_reg)) = 0x03;

    // Reset the Fifos in the FiFo control reg by settings bits 1 & 2
    ACCESS_ADDR(TRANSLATE_ADDR(_fifo_control_reg)) = 0x06;

    // Clear all bits in the FiFo control regs
    ACCESS_ADDR(TRANSLATE_ADDR(_fifo_control_reg)) = 0x00;
}

char UART::put_char(char c)
{
    while (((ACCESS_ADDR(TRANSLATE_ADDR(_line_status_reg))) & 0x20) != 0x20)
        ;

    ACCESS_ADDR(TRANSLATE_ADDR(_transmitter_fifo)) = c;

    return c;
}

char UART::get_char(long timeout)
{
    for (int cycles = 0; !test_for_data() && cycles < timeout; cycles++)
        ;

    return ACCESS_ADDR(TRANSLATE_ADDR(_receiver_fifo));
}

bool UART::test_for_data()
{
    return (ACCESS_ADDR(TRANSLATE_ADDR(_line_status_reg)) & 0x1) == 0x1;
}

void UART::flush(bool print_to_stdout)
{
    while (test_for_data())
    {
        char read = ACCESS_ADDR(TRANSLATE_ADDR(_receiver_fifo));
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
        put_char(c);
    }
}

void UART::wait_until_char(char c, long timeout_cycles)
{
    char read;
    do
    {
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
    do
    {
        read_char = get_char();
        if (read_char != c) {
            read += read_char;
        }
    } while (read_char != c);
    return read;
}