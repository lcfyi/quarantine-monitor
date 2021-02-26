/*
 * terasic_includes.h
 *
 *  Created on: 2017Äê1ÔÂ5ÈÕ
 *      Author: Administrator
 */

#ifndef TERASIC_INCLUDES_H_
#define TERASIC_INCLUDES_H_

#include <stdio.h>
#include <stdlib.h> // malloc, free
#include <string.h>
#include <stddef.h>
#include <unistd.h>  // usleep (unix standard?)
#include "sys/alt_flash.h"
#include "sys/alt_flash_types.h"
#include "io.h"
#include "alt_types.h"  // alt_u32
#include "altera_avalon_pio_regs.h" //IOWR_ALTERA_AVALON_PIO_DATA
#include "sys/alt_irq.h"  // interrupt
#include "sys/alt_alarm.h" // time tick function (alt_nticks(), alt_ticks_per_second())
#include "sys/alt_timestamp.h"
#include "sys/alt_stdio.h"
#include "system.h"
#include <fcntl.h>


#define DEBUG_DUMP  /*printf */

#include <stdbool.h>
enum {FALSE, TRUE};
//typedef int bool;
//#define TRUE    1
//#define FALSE   0


typedef alt_u8 	uint8_t;
typedef alt_8 	int8_t;
typedef alt_u16 uint16_t;
typedef alt_16 	int16_t;
typedef alt_u32 uint32_t;
typedef alt_32 	int32_t;

#endif /* TERASIC_INCLUDES_H_ */
