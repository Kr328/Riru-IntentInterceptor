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

static void appProcessPre(JNIEnv *env, int uid, jstring appDataDir) {
    if (appDataDir) {
        int user_id = uid / 100000;
        char package_name[PATH_MAX] = {0};

        const char *app_data_dir = (*env)->GetStringUTFChars(env, appDataDir, NULL);

        // /data/user/<user_id>/<package>
        if (sscanf(app_data_dir, "/data/%*[^/]/%*[^/]/%s", package_name) == 1)
            goto found;

        // /mnt/expand/<id>/user/<user_id>/<package>
        if (sscanf(appDataDir, "/mnt/expand/%*[^/]/%*[^/]/%*[^/]/%s", package_name) == 1)
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
        jstring *instructionSet, jstring *appDataDir, jboolean *isTopApp, jobjectArray *pkgDataInfoList,
        jobjectArray *whitelistedDataInfoList, jboolean *bindMountAppDataDirs, jboolean *bindMountAppStorageDirs) {
    appProcessPre(env, *uid, *appDataDir);
}

static void nativeSpecializeAppProcessPre(
        JNIEnv *env, jclass cls, jint *uid, jint *gid, jintArray *gids, jint *runtimeFlags,
        jobjectArray *rlimits, jint *mountExternal, jstring *seInfo, jstring *niceName,
        jboolean *startChildZygote, jstring *instructionSet, jstring *appDataDir,
        jboolean *isTopApp, jobjectArray *pkgDataInfoList, jobjectArray *whitelistedDataInfoList,
        jboolean *bindMountAppDataDirs, jboolean *bindMountAppStorageDirs) {
    appProcessPre(env, *uid, *appDataDir);
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

EXPORT
void *init(void *arg) {
    static void *_module = NULL;
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
                    RiruModuleInfoV9 *module = malloc(sizeof(RiruModuleInfoV9));
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

                    _module = module;

                    return module;
                }
                case 10: {
                    RiruModuleInfoV10 *module = malloc(sizeof(RiruModuleInfoV10));
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

                    _module = module;

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
            free(_module);

            return NULL;
        }
        default: {
            return NULL;
        }
    }
}