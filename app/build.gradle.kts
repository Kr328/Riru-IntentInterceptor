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

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":hideapi"))
}