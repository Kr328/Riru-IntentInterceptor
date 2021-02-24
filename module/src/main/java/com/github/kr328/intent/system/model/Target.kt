package com.github.kr328.intent.system.model

import com.github.kr328.intent.util.Json
import com.github.kr328.intent.util.JsonFactory
import com.github.kr328.intent.util.toList
import org.json.JSONArray
import org.json.JSONObject

data class Target(val packageName: String, val modules: Set<String>) : Json {
    override fun toJson(): JSONObject {
        return JSONObject()
            .put("packageName", packageName)
            .put("modules", JSONArray(modules))
    }

    companion object : JsonFactory<Target> {
        override fun fromJson(obj: JSONObject): Target {
            return Target(
                obj.getString("packageName"),
                obj.getJSONArray("modules").toList<String>().toSet()
            )
        }
    }
}
