#pragma once

#include <jni.h>

#define DEX_PATH "runtime/runtime.dex"
#define INJECT_CLASS_NAME "com.github.kr328.intent.InjectorKt"
#define INJECT_METHOD_NAME "main"
#define INJECT_METHOD_SIGNATURE "([Ljava/lang/String;)V"
#define OPTIMIZED_DIRECTORY "/data/dalvik-cache"

void attach_magisk_path(const char *magisk_path);

void read_dex_data();

void free_dex_data();

void load_and_invoke_dex(JNIEnv *env, const char *args);