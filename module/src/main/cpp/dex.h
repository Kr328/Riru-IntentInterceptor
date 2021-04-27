#pragma once

#include <jni.h>

#define DEX_PATH "/framework/" RIRU_DEX_NAME
#define DEX_IN_SYSTEM_PATH "/system" DEX_PATH
#define INJECT_CLASS_NAME "com.github.kr328.intent.InjectorKt"
#define INJECT_METHOD_NAME "main"
#define INJECT_METHOD_SIGNATURE "([Ljava/lang/String;)V"
#define OPTIMIZED_DIRECTORY "/data/dalvik-cache"

void read_dex_data(const char *riru_magisk_module_path);
void free_dex_data();
void load_and_invoke_dex(JNIEnv *env, const char *args);