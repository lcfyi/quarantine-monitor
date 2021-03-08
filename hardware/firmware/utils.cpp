#include "utils.h"

void naiveSleep(long timeMs) {
    long a = 0;
    do {
        a += 1;
    } while (a < (timeMs * 10000));
}
