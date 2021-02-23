package com.github.kr328.intent;

import com.github.kr328.intent.remote.Injection;

interface IIntentInterceptorService {
    List<Injection> open(String packageName);
}