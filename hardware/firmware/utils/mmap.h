#ifndef _MMAP_H_
#define _MMAP_H_

#include <fcntl.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/mman.h>

#define MMAP_FILE "/dev/mem"

#define HW_REGS_BASE 0xff200000
#define HW_REGS_SPAN 0x00200000
#define HW_REGS_MASK HW_REGS_SPAN - 1

extern char * virtual_base; // This will be set in main.cpp

#define TRANSLATE_ADDR(x) (virtual_base + ((x) & (HW_REGS_MASK)))

#define ACCESS_ADDR(x) (*((volatile unsigned char *)(x)))

/**
 * This MUST be run first before everything else works.
 * It's essentially our bootstrap.
 */
bool initialize_mmap();

#endif