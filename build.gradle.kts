@file:Suppress("UNUSED_VARIABLE")

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlinVersion = "1.4.30"

    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.1.2")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath(kotlin("serialization", version = kotlinVersion))

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

    val moduleMinSdkVersion: Int by extra(27)
    val moduleTargetSdkVersion: Int by extra(30)
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}

gradle.projectsEvaluated {
    project(":module").tasks["assembleDebug"].dependsOn(project(":app").tasks["assembleDebug"])
    project(":module").tasks["assembleRelease"].dependsOn(project(":app").tasks["assembleRelease"])
}