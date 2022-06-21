package com.github.kr328.intent.system

object InjectionManager {
    class Token() {

    }

    suspend fun withToken(block: suspend (Token) -> Unit) {
        val token = Token()

        try {

        } finally {

        }
    }
}