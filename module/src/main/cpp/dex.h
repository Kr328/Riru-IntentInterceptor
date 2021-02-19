#pragma once

#include <jni.h>

#define INJECT_DEX_FILE "boot-intent-interceptor.dex"
#define INJECT_DEX_PATH "/system/framework/" INJECT_DEX_FILE
#define INJECT_CLASS_NAME "com.github.kr328.intent.Injector"
#define INJECT_METHOD_NAME "inject"
#define INJECT_METHOD_SIGNATURE "(Ljava/lang/String;)V"

void read_dex_data();
void free_dex_data();
void load_and_invoke_dex(JNIEnv *env, const char *args);