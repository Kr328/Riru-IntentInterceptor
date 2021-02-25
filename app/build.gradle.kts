import java.util.*

plugins {
    id("com.android.application")
}

val moduleVersionCode: Int by extra
val moduleVersionName: String by extra
val moduleMinSdkVersion: Int by extra
val moduleTargetSdkVersion: Int by extra

android {
    compileSdkVersion(moduleTargetSdkVersion)

    defaultConfig {
        applicationId("com.github.kr328.intent")

        minSdkVersion(moduleMinSdkVersion)
        targetSdkVersion(moduleTargetSdkVersion)

        versionCode = moduleVersionCode
        versionName = moduleVersionName
    }

    signingConfigs {
        maybeCreate("release").apply {
            if (rootProject.file("keystore.properties").exists()) {
                val properties = Properties().apply {
                    rootProject.file("keystore.properties").inputStream().use {
                        load(it)
                    }
                }

                storeFile = rootProject.file(Objects.requireNonNull(properties.getProperty("storeFile", "")))
                storePassword = Objects.requireNonNull(properties.getProperty("storePassword", ""))
                keyAlias = Objects.requireNonNull(properties.getProperty("keyAlias", ""))
                keyPassword = Objects.requireNonNull(properties.getProperty("keyPassword", ""))
            }
        }
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.findByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        named("debug") {
            isMinifyEnabled = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

}