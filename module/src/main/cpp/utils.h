#pragma once

#include <jni.h>

#define scope_fd __attribute__((cleanup(cleanup_fd)))

// resource manage
void cleanup_fd(const int *fd);

// io
int write_full(int fd, const void *buf, unsigned length);
int read_full(int fd, void *buf, unsigned length);

