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

#define TARGET_RIRU_API 10

#define EXPORT __attribute__((visibility("default"))) __attribute__((used))
#define UNUSED(var) ((void) var)

static char saved_package_name[PATH_MAX];

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
        load_and_invoke_dex(env, "system_server");
    }
}

static void appProcessPre(JNIEnv *env, jstring *jAppDataDir) {
    memset(saved_package_name, 0, PATH_MAX);

    if (*jAppDataDir) {
        const char *appDataDir = (*env)->GetStringUTFChars(env, jAppDataDir, NULL);

        // /data/user/<user_id>/<package>
        if (sscanf(appDataDir, "/data/%*[^/]/%*[^/]/%s", saved_package_name) == 1)
            goto found;

        // /mnt/expand/<id>/user/<user_id>/<package>
        if (sscanf(appDataDir, "/mnt/expand/%*[^/]/%*[^/]/%*[^/]/%s", saved_package_name) == 1)
            goto found;

        // /data/data/<package>
        if (sscanf(appDataDir, "/data/%*[^/]/%s", saved_package_name) == 1)
            goto found;

        // nothing found
        saved_package_name[0] = '\0';

        found:;
    }
}

static void appProcessPost(JNIEnv *env, const char *from, const char *package_name, jint uid) {
    LOGD("%s: uid=%d, package=%s", from, uid, package_name);

    if (should_inject_package(package_name)) {
        load_and_invoke_dex(env, "app");
    } else {
        free_dex_data();
    }
}

static void nativeForkAndSpecializePre(
        JNIEnv *env, jclass clazz, jint *uid, jint *gid, jintArray *gids, jint *runtimeFlags,
        jobjectArray *rlimits, jint *mountExternal, jstring *seInfo, jstring *niceName,
        jintArray *fdsToClose, jintArray *fdsToIgnore, jboolean *is_child_zygote,
        jstring *instructionSet, jstring *appDataDir, jboolean *isTopApp,
        jobjectArray *pkgDataInfoList,
        jobjectArray *whitelistedDataInfoList, jboolean *bindMountAppDataDirs,
        jboolean *bindMountAppStorageDirs) {
    appProcessPre(env, appDataDir);
}

static void nativeForkAndSpecializePost(JNIEnv *env, jclass clazz, jint res) {
    if (res == 0) {
        appProcessPost(env, "forkAndSpecialize", saved_package_name, getuid());
    }
}

static void nativeSpecializeAppProcessPre(
        JNIEnv *env, jclass clazz, jint *uid, jint *gid, jintArray *gids, jint *runtimeFlags,
        jobjectArray *rlimits, jint *mountExternal, jstring *seInfo, jstring *niceName,
        jboolean *startChildZygote, jstring *instructionSet, jstring *appDataDir,
        jboolean *isTopApp, jobjectArray *pkgDataInfoList, jobjectArray *whitelistedDataInfoList,
        jboolean *bindMountAppDataDirs, jboolean *bindMountAppStorageDirs) {
    appProcessPre(env, appDataDir);
}

static void nativeSpecializeAppProcessPost(
        JNIEnv *env, jclass clazz) {
    appProcessPost(env, "specializeAppProcess", saved_package_name, getuid());
}

EXPORT
void *init(void *arg) {
    static RiruModuleInfoV9 *module;
    static int riru_api_version = -1;
    static int phase = 0;

    phase++;

    switch (phase) {
        case 1: {
            int core_max_api_version = *(int *) arg;
            riru_api_version = core_max_api_version <= TARGET_RIRU_API ? core_max_api_version
                                                                       : TARGET_RIRU_API;
            return &riru_api_version;
        }
        case 2: {
            switch (riru_api_version) {
                case 9: {
                    module = malloc(sizeof(RiruModuleInfoV9));
                    memset(module, 0, sizeof(*module));

                    module->supportHide = 1;

                    module->versionName = RIRU_MODULE_VERSION_NAME;
                    module->version = RIRU_MODULE_VERSION_CODE;

                    module->onModuleLoaded = &onModuleLoaded;
                    module->shouldSkipUid = &shouldSkipUid;
                    module->forkSystemServerPost = &nativeForkSystemServerPost;
                    module->forkAndSpecializePre = &nativeForkAndSpecializePre;
                    module->forkAndSpecializePost = &nativeForkAndSpecializePost;
                    module->specializeAppProcessPre = &nativeSpecializeAppProcessPre;
                    module->specializeAppProcessPost = &nativeSpecializeAppProcessPost;

                    return module;
                }
                case 10: {
                    module = malloc(sizeof(RiruModuleInfoV10));
                    memset(module, 0, sizeof(*module));

                    module->supportHide = 1;

                    module->versionName = RIRU_MODULE_VERSION_NAME;
                    module->version = RIRU_MODULE_VERSION_CODE;

                    module->onModuleLoaded = &onModuleLoaded;
                    module->shouldSkipUid = &shouldSkipUid;
                    module->forkSystemServerPost = &nativeForkSystemServerPost;
                    module->forkAndSpecializePre = &nativeForkAndSpecializePre;
                    module->forkAndSpecializePost = &nativeForkAndSpecializePost;
                    module->specializeAppProcessPre = &nativeSpecializeAppProcessPre;
                    module->specializeAppProcessPost = &nativeSpecializeAppProcessPost;

                    return module;
                }
                case -1: {
                    LOGE("invalid riru api version");

                    break;
                }
                default: {
                    break;
                }
            }

            return NULL;
        }
        case 3: {
            free(module);

            return NULL;
        }
        default: {
            return NULL;
        }
    }
}