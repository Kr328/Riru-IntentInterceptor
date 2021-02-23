#pragma once

#define CONFIGURATION_PATH "/data/misc/intent"
#define FORMAT_TARGET CONFIGURATION_PATH "/%d/targets/%s.json"

int should_inject_packages(int user_id, const char *package_name);