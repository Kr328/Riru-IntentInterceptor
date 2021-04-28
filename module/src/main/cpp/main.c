#pragma clang diagnostic push
#pragma ide diagnostic ignored "cert-err34-c"

#include <stdio.h>
#include <jni.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <limits.h>
#include <fcntl.h>
#include <errno.h>
#include <android/log.h>
#include <sys/stat.h>
#include <sys/system_properties.h>

#include "riru.h"
#include "log.h"
#include "utils.h"
#include "dex.h"
#include "config.h"

#define RIRU_TARGET_API 25

#define EXPORT __attribute__((visibility("default"))) __attribute__((used))
#define UNUSED(var) ((void) var)

static Riru *riru_api;
static int inject_next_app = 0;

static void onModuleLoaded() {
    read_dex_data();
}

static int shouldSkipUid(int uid) {
    UNUSED(uid);

    return 0;
}

static void nativeForkSystemServerPost(JNIEnv *env, jclass clazz, jint res) {
    UNUSED(clazz);

    if (res == 0) {
        load_and_invoke_dex(env, "system");
    }
}

static void appProcessPre(JNIEnv *env, jstring appDataDir) {
    if (appDataDir) {
        int user_id = 0;
        char package_name[PATH_MAX] = {0};

        const char *app_data_dir = (*env)->GetStringUTFChars(env, appDataDir, NULL);

        // /data/user/<user_id>/<package>
        if (sscanf(app_data_dir, "/data/%*[^/]/%d/%s", &user_id, package_name) == 2)
            goto found;

        // /mnt/expand/<id>/user/<user_id>/<package>
        if (sscanf(appDataDir, "/mnt/expand/%*[^/]/%*[^/]/%d/%s", &user_id, package_name) == 2)
            goto found;

        // /data/data/<package>
        if (sscanf(appDataDir, "/data/%*[^/]/%s", package_name) == 1)
            goto found;

        // nothing found
        package_name[0] = 0;

        found:

        inject_next_app = should_inject_packages(user_id, package_name);

        (*env)->ReleaseStringUTFChars(env, appDataDir, app_data_dir);
    } else {
        inject_next_app = 0;
    }

    *riru_api->allowUnload = !inject_next_app;
}

static void appProcessPost(JNIEnv *env) {
    if (inject_next_app) {
        load_and_invoke_dex(env, "app");
    } else {
        free_dex_data();
    }
}

static void nativeForkAndSpecializePre(
        JNIEnv *env, jclass cls, jint *uid, jint *gid, jintArray *gids, jint *runtimeFlags,
        jobjectArray *rlimits, jint *mountExternal, jstring *seInfo, jstring *niceName,
        jintArray *fdsToClose, jintArray *fdsToIgnore, jboolean *is_child_zygote,
        jstring *instructionSet, jstring *appDataDir, jboolean *isTopApp,
        jobjectArray *pkgDataInfoList,
        jobjectArray *whitelistedDataInfoList, jboolean *bindMountAppDataDirs,
        jboolean *bindMountAppStorageDirs) {
    appProcessPre(env, *appDataDir);
}

static void nativeSpecializeAppProcessPre(
        JNIEnv *env, jclass cls, jint *uid, jint *gid, jintArray *gids, jint *runtimeFlags,
        jobjectArray *rlimits, jint *mountExternal, jstring *seInfo, jstring *niceName,
        jboolean *startChildZygote, jstring *instructionSet, jstring *appDataDir,
        jboolean *isTopApp, jobjectArray *pkgDataInfoList, jobjectArray *whitelistedDataInfoList,
        jboolean *bindMountAppDataDirs, jboolean *bindMountAppStorageDirs) {
    appProcessPre(env, *appDataDir);
}

static void nativeForkAndSpecializePost(JNIEnv *env, jclass clazz, jint res) {
    if (res == 0) {
        appProcessPost(env);
    }
}

static void nativeSpecializeAppProcessPost(
        JNIEnv *env, jclass clazz) {
    appProcessPost(env);
}

RiruVersionedModuleInfo module = {
        .moduleApiVersion = RIRU_TARGET_API,
        .moduleInfo = {
                .supportHide = true,
                .version = RIRU_MODULE_VERSION_CODE,
                .versionName = RIRU_MODULE_VERSION_NAME,
                .onModuleLoaded = onModuleLoaded,
                .shouldSkipUid = shouldSkipUid,
                .forkAndSpecializePre = nativeForkAndSpecializePre,
                .forkAndSpecializePost = nativeForkAndSpecializePost,
                .forkSystemServerPre = NULL,
                .forkSystemServerPost = nativeForkSystemServerPost,
                .specializeAppProcessPre = nativeSpecializeAppProcessPre,
                .specializeAppProcessPost = nativeSpecializeAppProcessPost,
        }
};

EXPORT
RiruVersionedModuleInfo *init(Riru *riru) {
    if (riru->riruApiVersion < RIRU_TARGET_API) return NULL;

    riru_api = riru;

    *riru->allowUnload = true;

    attach_magisk_path(strdup(riru->magiskModulePath));

    return &module;
}

#pragma clang diagnostic pop