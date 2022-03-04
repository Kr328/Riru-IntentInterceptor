@file:Suppress("UNUSED_VARIABLE")

import com.android.build.gradle.BaseExtension

buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://maven.kr328.app/releases")
    }
    dependencies {
        classpath(deps.build.android)
        classpath(deps.build.kotlin)
        classpath(deps.build.ksp)
        classpath(deps.build.zloader)
        classpath(deps.build.refine)
    }
}

subprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://maven.kr328.app/releases")
    }

    val isApp = name in setOf("app", "module")

    apply(plugin = if (isApp) "com.android.application" else "com.android.library")

    extensions.configure<BaseExtension> {
        compileSdkVersion(31)

        defaultConfig {
            if (isApp) {
                applicationId = "com.github.kr328.intent"
            }

            minSdk = 26
            targetSdk = 31
            versionName = "2.0"
            versionCode = 200

            if (!isApp) {
                consumerProguardFiles("consumer-rules.pro")
            }
        }

        buildTypes {
            named("release") {
                isMinifyEnabled = isApp
                isShrinkResources = isApp
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }
    }
}

task("clean", type = Delete::class) {
    group = "build"

    delete(rootProject.buildDir)
}