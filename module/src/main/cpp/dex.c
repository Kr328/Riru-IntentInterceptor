#include "dex.h"
#include "utils.h"
#include "log.h"

#include <fcntl.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <malloc.h>
#include <stdlib.h>
#include <sys/stat.h>
#include <sys/system_properties.h>

#define assert_syscall(condition, message) if (condition) do {LOGE("zygote: " message ": %s", strerror(errno)); return;} while (0)
#define assert_load(message) if (catch_exception(env)) do {LOGE("zygote: load dex: " message " failure"); return;} while (0)

static void *dex_data;
static size_t dex_size;

static int get_platform_api_version() {
    int version = 0;
    char buffer[PROP_VALUE_MAX];

    if (__system_property_get("ro.build.version.sdk", buffer)) {
        version = strtol(buffer, NULL, 10);
    }

    return version;
}

void read_dex_data() {
    if (dex_data != NULL)
        return;

    // InMemoryClassLoader unavailable in android 6.0-7.0
    if (get_platform_api_version() < 26)
        return;

    scope_fd int fd = open(DEX_PATH, O_RDONLY);
    assert_syscall(fd < 0, "open dex file");

    struct stat s;
    assert_syscall(fstat(fd, &s) < 0, "stat dex");

    dex_data = malloc(s.st_size);

    assert_syscall(read_full(fd, dex_data, s.st_size) < 0, "read dex file");

    dex_size = s.st_size;

    LOGI("zygote: dex " DEX_PATH);
}

void free_dex_data() {
    free(dex_data);
    dex_data = NULL;
}

static int catch_exception(JNIEnv *env) {
    int result = (*env)->ExceptionCheck(env);

    // check status
    if (result) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
    }

    return result;
}

static jobject load_dex_in_memory(JNIEnv *env, jobject oSystemClassLoader) {
    if (dex_size == 0 || get_platform_api_version() < 26)
        return NULL;

    jobject bufferDex = (*env)->NewDirectByteBuffer(env, dex_data, dex_size);
    jclass cDexClassLoader = (*env)->FindClass(env, "dalvik/system/InMemoryDexClassLoader");
    jmethodID mDexClassLoaderInit = (*env)->GetMethodID(env, cDexClassLoader, "<init>",
                                                        "(Ljava/nio/ByteBuffer;Ljava/lang/ClassLoader;)V");
    jobject oClassLoader = (*env)->NewObject(env, cDexClassLoader,
                                             mDexClassLoaderInit,
                                             bufferDex,
                                             oSystemClassLoader);

    if (catch_exception(env)) {
        return NULL;
    }

    return oClassLoader;
}

static jobject load_dex_by_file(JNIEnv *env, jobject oSystemClassLoader) {
    jclass cDexClassLoader = (*env)->FindClass(env, "dalvik/system/DexClassLoader");
    jmethodID mDexClassLoaderInit = (*env)->GetMethodID(env, cDexClassLoader, "<init>",
                                                        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/ClassLoader;)V");

    jobject oClassLoader = (*env)->NewObject(env, cDexClassLoader,
                                             mDexClassLoaderInit,
                                             (*env)->NewStringUTF(env, DEX_PATH),
                                             (*env)->NewStringUTF(env, OPTIMIZED_DIRECTORY),
                                             NULL,
                                             oSystemClassLoader
    );

    if (catch_exception(env)) {
        return NULL;
    }

    return oClassLoader;
}

void load_and_invoke_dex(JNIEnv *env, const char *args) {
    if (dex_size == 0)
        return;

    // get system class loader
    jclass cClassLoader = (*env)->FindClass(env, "java/lang/ClassLoader");
    jmethodID mGetSystemClassLoader = (*env)->GetStaticMethodID(env, cClassLoader,
            "getSystemClassLoader", "()Ljava/lang/ClassLoader;");
    jobject oSystemClassLoader = (*env)->CallStaticObjectMethod(env, cClassLoader, mGetSystemClassLoader);

    assert_load("ClassLoader.getSystemClassLoader(...)");

    // load dex
    jobject oDexClassLoader = load_dex_in_memory(env, oSystemClassLoader);

    if (oDexClassLoader == NULL) {
        oDexClassLoader = load_dex_by_file(env, oSystemClassLoader);
    }

    if (oDexClassLoader == NULL) {
        LOGE("zygote: load dex failure");

        return;
    }

    // get loaded dex inject method
    jmethodID mFindClass = (*env)->GetMethodID(env, cClassLoader, "loadClass",
                                               "(Ljava/lang/String;)Ljava/lang/Class;");
    jstring sInjectClassName = (*env)->NewStringUTF(env, INJECT_CLASS_NAME);
    jclass cInject = (jclass) (*env)->CallObjectMethod(env, oDexClassLoader,
                                                       mFindClass, sInjectClassName);

    assert_load("loadClass(...)");

    // find method
    jmethodID mLoaded = (*env)->GetStaticMethodID(env, cInject, INJECT_METHOD_NAME,
                                                  INJECT_METHOD_SIGNATURE);

    assert_load("getMethod(...)");

    // invoke inject method
    jobjectArray argsArray = (*env)->NewObjectArray(env, 1,
                                                    (*env)->FindClass(env, "java/lang/String"),
                                                    (*env)->NewStringUTF(env, args));

    (*env)->CallStaticVoidMethod(env, cInject, mLoaded, argsArray);

    assert_load("invoke(...)");
}