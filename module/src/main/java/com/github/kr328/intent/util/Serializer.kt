package com.github.kr328.intent.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.File

fun <T> File.readJson(serializer: KSerializer<T>, json: Json = Json.Default): T {
    return json.decodeFromString(serializer, readText())
}

fun <T> T.writeJson(serializer: KSerializer<T>, file: File, json: Json = Json.Default) {
    file.writeText(json.encodeToString(serializer, this))
}

fun <K, T> File.loadJsonFiles(
    serializer: KSerializer<T>,
    keyExtractor: (T) -> K,
    json: Json = Json.Default
): Map<K, T> {
    return (listFiles() ?: emptyArray()).asSequence()
        .filter { it.extension == "json" }
        .map { it.readJson(serializer, json) }
        .map { keyExtractor(it) to it }
        .toMap()
}

fun <K, T> Map<K, T>.storeJsonFiles(
    serializer: KSerializer<T>,
    fileName: (Map.Entry<K, T>) -> String,
    directory: File,
    json: Json = Json.Default
) {
    directory.deleteRecursively()
    directory.mkdirs()

    return forEach { entry ->
        entry.value.writeJson(serializer, directory.resolve(fileName(entry) + ".json"), json)
    }
}