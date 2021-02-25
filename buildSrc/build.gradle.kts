plugins {
    kotlin("jvm") version "1.4.30"
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
    google()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.android.tools.build:gradle:4.1.2") {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk7")
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }
    implementation("com.google.guava:guava:30.1-jre")
    implementation("org.smali:dexlib2:2.4.0") {
        exclude("com.google.guava", "guava")
    }
}

gradlePlugin {
    plugins {
        create("riru") {
            id = "riru"
            implementationClass = "RiruPlugin"
        }
    }
}