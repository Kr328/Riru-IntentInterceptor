#include "config.h"
#include "log.h"
#include "utils.h"

#include <unistd.h>
#include <limits.h>
#include <stdio.h>

int should_inject_package(const char *package_name) {
    char buffer[PATH_MAX];

    sprintf(buffer, PACKAGES_PATH "/%s.json", package_name);

    return access(buffer, F_OK) == 0;
}
