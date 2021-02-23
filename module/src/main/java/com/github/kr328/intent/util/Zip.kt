package com.github.kr328.intent.util

import android.os.SharedMemory
import android.system.OsConstants
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

fun File.unzipToSharedMemory(filter: (ZipEntry) -> Boolean): List<SharedMemory> {
    val buffer = ByteArray(1024)

    return ZipFile(this).use { zip ->
        zip.entries().asSequence().filter(filter).map { entry ->
            zip.getInputStream(entry).use {
                SharedMemory.create(null, entry.size.toInt()).apply {
                    val m = mapReadWrite()

                    var size = size

                    while (size > 0) {
                        val r = it.read(buffer)

                        m.put(buffer, 0, r)

                        size -= r
                    }

                    SharedMemory.unmap(m)

                    setProtect(OsConstants.PROT_READ)
                }
            }
        }.toList()
    }
}
