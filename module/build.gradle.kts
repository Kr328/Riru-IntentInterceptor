import com.github.kr328.zloader.gradle.ZygoteLoader.PACKAGE_SYSTEM_SERVER

plugins {
    kotlin("android")
    id("com.android.application")
    id("com.google.devtools.ksp")
    id("zygote-loader")
    id("dev.rikka.tools.refine.gradle-plugin")
}

android {
    sourceSets {
        all {
            kotlin.srcDir(buildDir.resolve("generated/ksp/$name/kotlin"))
        }
    }
}

dependencies {
    compileOnly(project(":hideapi"))

    implementation(project(":shared"))

    ksp(deps.kaidl.compiler)
    compileOnly(deps.kaidl.runtime)
    implementation(deps.kotlin.coroutine)
    implementation(deps.magic.library)
}
zygote {
    val moduleId = "intent-interceptor"
    val moduleName = "Intent Interceptor"
    val moduleDescription = "Allows plugins to intercept app's intents."
    val moduleAuthor = "Kr328"
    val moduleEntrypoint = "com.github.kr328.intent.MainKt"
    val versionName = android.defaultConfig.versionName

    packages(PACKAGE_SYSTEM_SERVER)

    riru {
        archiveName = "riru-$moduleId-$versionName"
        updateJson = "https://github.com/Kr328/Riru-IFWEnhance/releases/latest/download/riru-$moduleId.json"
    }

    zygisk {
        archiveName = "zygisk-$moduleId-$versionName"
        updateJson = "https://github.com/Kr328/Riru-IFWEnhance/releases/latest/download/zygisk-$moduleId.json"
    }

    all {
        id = moduleId
        name = moduleName
        author = moduleAuthor
        description = moduleDescription
        entrypoint = moduleEntrypoint
    }
}