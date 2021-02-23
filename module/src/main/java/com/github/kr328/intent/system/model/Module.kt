package com.github.kr328.intent.system.model

import kotlinx.serialization.Serializable

@Serializable
data class Module(val packageName: String, val interceptor: String, val target: List<String>)
