package com.github.kr328.intent.system.model

import com.github.kr328.intent.util.Json
import com.github.kr328.intent.util.JsonFactory
import com.github.kr328.intent.util.toList
import org.json.JSONArray
import org.json.JSONObject

data class Module(
    val packageName: String,
    val interceptor: String,
    val targets: List<String>
) : Json {
    override fun toJson(): JSONObject {
        return JSONObject()
            .put("packageName", packageName)
            .put("interceptor", interceptor)
            .put("targets", JSONArray(targets))
    }

    companion object : JsonFactory<Module> {
        override fun fromJson(obj: JSONObject): Module {
            return Module(
                obj.getString("packageName"),
                obj.getString("interceptor"),
                obj.getJSONArray("targets").toList()
            )
        }
    }
}
