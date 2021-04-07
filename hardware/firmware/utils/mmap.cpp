#include "mmap.h"


char *virtual_base;

bool initialize_mmap()
{
    int fd;

    if ((fd = open(MMAP_FILE, O_RDWR | O_SYNC)) < 0)
    {
        printf("ERROR: Failed to open up mmap.\n");
        exit(1);
    }

    virtual_base = (char *)mmap(
        NULL,
        HW_REGS_SPAN,
        PROT_READ | PROT_WRITE,
        MAP_SHARED,
        fd,
        HW_REGS_BASE);

    if (virtual_base == MAP_FAILED)
    {
        printf("ERROR: mmap() failed.\n");
        close(fd);
        exit(1);
    }
    return true;
}