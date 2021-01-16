@file:Suppress("UNUSED_VARIABLE")

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-alpha04")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.21")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }

    val moduleVersionCode: Int by extra(1)
    val moduleVersionName: String by extra("1.0.0")

    val moduleMinSdkVersion: Int by extra(26)
    val moduleTargetSdkVersion: Int by extra(30)

    val kotlinVersion: String by extra("1.4.21")
    val composeVersion: String by extra("1.0.0-alpha10")
    val ktxVersion: String by extra("1.3.2")
    val appcompatVersion: String by extra("1.2.0")
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}
