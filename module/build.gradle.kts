plugins {
    id("com.android.application")
    kotlin("android")
    id("hideapi-redefine")
    id("riru")
}

val moduleVersionCode: Int by extra
val moduleVersionName: String by extra
val moduleMinSdkVersion: Int by extra
val moduleTargetSdkVersion: Int by extra

riru {
    id = "riru_intent_interceptor"
    name = "Riru - Intent Interceptor"
    description = "A module of Riru. Allow modules modify activity intents."
    author = "Kr328"
    minApi = 25
    minApiName = "25.0"
}

android {
    compileSdkVersion(moduleTargetSdkVersion)

    ndkVersion = "22.0.7026061"

    defaultConfig {
        applicationId = "com.github.kr328.intent"

        minSdkVersion(moduleMinSdkVersion)
        targetSdkVersion(moduleTargetSdkVersion)

        versionCode = moduleVersionCode
        versionName = moduleVersionName

        multiDexEnabled = false

        externalNativeBuild {
            cmake {
                arguments(
                    "-DRIRU_NAME:STRING=${riru.name}",
                    "-DRIRU_MODULE_ID:STRING=${riru.riruId}",
                    "-DRIRU_MODULE_VERSION_CODE:INTEGER=$versionCode",
                    "-DRIRU_MODULE_VERSION_NAME:STRING=$versionName"
                )
            }
        }
    }

    buildFeatures {
        prefab = true
    }

    buildTypes {
        named("debug") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules-debug.pro"
            )
        }
        named("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules-release.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
}

dependencies {
    compileOnly(project(":hideapi"))

    implementation(project(":shared"))

    implementation(kotlin("stdlib"))
    implementation("androidx.annotation:annotation:1.2.0")

    implementation("dev.rikka.ndk:riru:25.0.0")
}

afterEvaluate {
    android.applicationVariants.forEach {
        val cName = it.name.capitalize()

        val cp = tasks.register("copyModuleApk$cName", Copy::class.java) {
            from(project(":app").buildDir
                .resolve("outputs/apk/${it.name}/app-${it.name}.apk"))

            into(generatedMagiskDir(it)
                .resolve("system/priv-app/IntentInterceptor"))

            rename {
                "IntentInterceptor.apk"
            }
        }

        tasks["mergeMagisk$cName"].dependsOn(cp)
        cp.get().dependsOn(project(":app").tasks["assemble$cName"])
    }
}