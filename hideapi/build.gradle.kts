plugins {
    id("com.android.library")
}

val moduleVersionCode: Int by extra
val moduleVersionName: String by extra
val moduleMinSdkVersion: Int by extra
val moduleTargetSdkVersion: Int by extra

android {
    compileSdkVersion(moduleTargetSdkVersion)

    defaultConfig {
        minSdkVersion(moduleMinSdkVersion)
        targetSdkVersion(moduleTargetSdkVersion)

        versionCode = moduleVersionCode
        versionName = moduleVersionName
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

}

repositories {
    mavenCentral()
}