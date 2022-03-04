@file:Suppress("UnstableApiUsage")

enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "Riru-IntentInterceptor"

include(":app")
include(":hideapi")
include(":shared")
include(":module")

dependencyResolutionManagement {
    versionCatalogs {
        create("deps") {
            val agp = "7.1.1"
            val zloader = "2.3"
            val kotlin = "1.6.10"
            val ksp = "$kotlin-1.0.2"
            val magic = "1.4"
            val coroutine = "1.6.0"
            val kaidl = "1.15"
            val refine = "3.0.3"

            library("build-android", "com.android.tools.build:gradle:$agp")
            library("build-zloader", "com.github.kr328.zloader:gradle-plugin:$zloader")
            library("build-kotlin", "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin")
            library("build-ksp", "com.google.devtools.ksp:symbol-processing-gradle-plugin:$ksp")
            library("build-refine", "dev.rikka.tools.refine:gradle-plugin:$refine")
            library("magic-library", "com.github.kr328.magic:library:$magic")
            library("kotlin-coroutine", "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutine")
            library("kaidl-compiler", "com.github.kr328.kaidl:kaidl:$kaidl")
            library("kaidl-runtime", "com.github.kr328.kaidl:kaidl-runtime:$kaidl")
            library("refine-processor", "dev.rikka.tools.refine:annotation-processor:$refine")
            library("refine-annotations", "dev.rikka.tools.refine:annotation:$refine")
        }
    }
}