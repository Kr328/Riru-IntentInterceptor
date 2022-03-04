package com.github.kr328.intent.remote

import com.github.kr328.kaidl.BinderInterface

@BinderInterface
interface IIntentInterceptor {
    fun load(): List<Injection>
}