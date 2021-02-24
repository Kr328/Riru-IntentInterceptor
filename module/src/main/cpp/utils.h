#pragma once

#define scope_fd __attribute__((cleanup(cleanup_fd)))

// resource manage
void cleanup_fd(const int *fd);

int read_full(int fd, void *buf, unsigned length);

