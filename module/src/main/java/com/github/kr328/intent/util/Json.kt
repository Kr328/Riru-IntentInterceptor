package com.github.kr328.intent.util

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

interface Json {
    fun toJson(): JSONObject
}

interface JsonFactory<T> {
    fun fromJson(obj: JSONObject): T
}

inline fun <reified T> JSONArray.toList(): List<T> {
    return List(length()) { get(it) }.filterIsInstance<T>()
}

fun <T> File.loadJsonFiles(
    factory: JsonFactory<T>
): List<T> {
    return (listFiles() ?: emptyArray()).asSequence()
        .filter { it.extension == "json" }
        .map { factory.fromJson(JSONObject(it.readText())) }
        .toList()
}

fun <T : Json> Collection<T>.storeJsonFiles(
    fileName: (T) -> String,
    directory: File,
) {
    directory.deleteRecursively()
    directory.mkdirs()

    return forEach { entry ->
        directory.resolve(fileName(entry) + ".json").writeText(entry.toJson().toString())
    }
}