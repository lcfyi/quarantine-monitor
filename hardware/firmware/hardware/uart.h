#ifndef _UART_H_
#define _UART_H_

#include <string>
#include <stdio.h>
#include <unistd.h>
#include "../mmap.h"

#define TIMEOUT_CYCLES 10000000

class UART
{
protected:
    UART(int line_control_reg,
         int divisor_latch_lsb,
         int divisor_latch_msb,
         int fifo_control_reg,
         int transmitter_fifo,
         int receiver_fifo,
         int line_status_reg);

    void init(int baud_rate);
    char put_char(char c);
    char get_char(long timeout = TIMEOUT_CYCLES);
    bool test_for_data();
    void flush(bool print_to_stdout = false);
    void write(std::string);
    void wait_until_char(char c, long timeout_cycles);
    std::string read_until_char(char c);

    int _line_control_reg;
    int _divisor_latch_lsb;
    int _divisor_latch_msb;
    int _fifo_control_reg;
    int _transmitter_fifo;
    int _receiver_fifo;
    int _line_status_reg;
};

#endif
