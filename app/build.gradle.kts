plugins {
    id("com.android.application")
    id("kotlin-android")
}

val moduleVersionCode: Int by extra
val moduleVersionName: String by extra
val moduleMinSdkVersion: Int by extra
val moduleTargetSdkVersion: Int by extra

val ktxVersion: String by extra
val appcompatVersion: String by extra

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

    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("androidx.core:core-ktx:$ktxVersion")
    implementation("androidx.appcompat:appcompat:$appcompatVersion")
}