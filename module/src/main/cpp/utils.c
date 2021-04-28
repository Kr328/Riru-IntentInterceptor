#include "utils.h"

#include <stddef.h>
#include <fcntl.h>
#include <unistd.h>

void cleanup_fd(const int *fd) {
    if (fd != NULL && *fd >= 0) close(*fd);
}

int read_full(int fd, void *buf, unsigned int length) {
    unsigned _read = 0;

    while (_read < length) {
        int r = read(fd, buf + _read, length - _read);
        if (r < 0)
            return r;
        _read += r;
    }

    return (int) _read;
}
