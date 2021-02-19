#pragma once

#define CONFIGURATION_PATH "/data/misc/intent"
#define PACKAGES_PATH CONFIGURATION_PATH "/packages"

int should_inject_package(const char *package_name);
