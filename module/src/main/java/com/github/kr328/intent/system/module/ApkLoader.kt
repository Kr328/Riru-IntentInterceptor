package com.github.kr328.intent.system.module

import android.os.SharedMemory
import com.github.kr328.intent.compat.SystemService
import com.github.kr328.intent.util.unzipToSharedMemory
import java.io.File
import java.io.FileNotFoundException

private val REGEX_CLASSES_DEX = Regex("classes\\d+\\.dex")

data class LoadedApk(
    val packageName: String,
    val classesDex: List<SharedMemory>,
) : AutoCloseable {
    override fun close() {
        classesDex.forEach { it.close() }
    }
}

fun loadApk(packageName: String, userId: Int): LoadedApk {
    val pkg = SystemService.packages.getPackageInfo(packageName, 0, userId)
        ?: throw FileNotFoundException("package $packageName not found")

    val dex = (arrayOf(pkg.applicationInfo.sourceDir) +
            (pkg.applicationInfo.splitSourceDirs ?: emptyArray())).asSequence()
        .filterNotNull()
        .flatMap { file -> File(file).unzipToSharedMemory { it.name.matches(REGEX_CLASSES_DEX) } }
        .toList()

    return LoadedApk(packageName, dex)
}