@file:Suppress("UNUSED_VARIABLE")

allprojects {
    repositories {
        google()
        jcenter()
    }

    val moduleVersionCode: Int by extra(101)
    val moduleVersionName: String by extra("1.1")

    val moduleMinSdkVersion: Int by extra(23)
    val moduleTargetSdkVersion: Int by extra(30)
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}