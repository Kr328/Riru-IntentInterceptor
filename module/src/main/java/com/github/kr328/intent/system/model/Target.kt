package com.github.kr328.intent.system.model

import kotlinx.serialization.Serializable

@Serializable
data class Target(val packageName: String, val modules: Set<String>)
