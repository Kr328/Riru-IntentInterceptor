#pragma once

#include <jni.h>

#define DEX_FILE "boot-intent-interceptor.dex"
#define DEX_PATH "/system/framework/" DEX_FILE
#define INJECT_CLASS_NAME "com.github.kr328.intent.InjectorKt"
#define INJECT_METHOD_NAME "main"
#define INJECT_METHOD_SIGNATURE "([Ljava/lang/String;)V"

void read_dex_data();
void free_dex_data();
void load_and_invoke_dex(JNIEnv *env, const char *args);