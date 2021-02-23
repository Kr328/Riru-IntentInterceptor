#include "dex.h"
#include "utils.h"
#include "log.h"

#include <fcntl.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <malloc.h>
#include <sys/stat.h>

#define assert_syscall(condition, message) if (condition) do {LOGE("zygote: " message ": %s", strerror(errno)); return;} while (0)
#define assert_load(message) if (catch_exception(env)) do {LOGE("zygote: load dex: " message " failure"); return;} while (0)

static void *dex_data;
static size_t dex_size;

void read_dex_data() {
    if (dex_data != NULL)
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

void load_and_invoke_dex(JNIEnv *env, const char *args) {
    if (dex_size == 0)
        return;

    // get system class loader
    jclass cClassLoader = (*env)->FindClass(env, "java/lang/ClassLoader");
    jmethodID mSystemClassLoader = (*env)->GetStaticMethodID(env, cClassLoader,
                                                             "getSystemClassLoader",
                                                             "()Ljava/lang/ClassLoader;");
    jobject oSystemClassLoader = (*env)->CallStaticObjectMethod(env, cClassLoader,
                                                                mSystemClassLoader);

    assert_load("getSystemClassLoader(...)");

    // load dex
    jobject bufferDex = (*env)->NewDirectByteBuffer(env, dex_data, dex_size);
    jclass cDexClassLoader = (*env)->FindClass(env, "dalvik/system/InMemoryDexClassLoader");
    jmethodID mDexClassLoaderInit = (*env)->GetMethodID(env, cDexClassLoader, "<init>",
                                                        "(Ljava/nio/ByteBuffer;Ljava/lang/ClassLoader;)V");
    jobject oDexClassLoader = (*env)->NewObject(env, cDexClassLoader,
                                                mDexClassLoaderInit,
                                                bufferDex,
                                                oSystemClassLoader);

    assert_load("new InMemoryDexClassLoader(...)");

    // get loaded dex inject method
    jmethodID mFindClass = (*env)->GetMethodID(env, cDexClassLoader, "loadClass",
                                               "(Ljava/lang/String;Z)Ljava/lang/Class;");
    jstring sInjectClassName = (*env)->NewStringUTF(env, INJECT_CLASS_NAME);
    jclass cInject = (jclass) (*env)->CallObjectMethod(env, oDexClassLoader,
                                                       mFindClass, sInjectClassName, (jboolean) 0);

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