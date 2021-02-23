#include "config.h"
#include "log.h"
#include "utils.h"

#include <unistd.h>
#include <limits.h>
#include <stdio.h>

int should_inject_packages(int user_id, const char *package_name) {
    if (package_name[0] == 0)
        return 0;

    char path[PATH_MAX];

    sprintf(path, FORMAT_TARGET, user_id, package_name);

    return access(path, F_OK) == 0;
}
