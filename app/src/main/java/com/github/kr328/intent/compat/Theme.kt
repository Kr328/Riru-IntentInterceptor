package com.github.kr328.intent.compat

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes

fun Context.resolveThemedColor(@AttrRes resId: Int): Int {
    return TypedValue().apply {
        theme.resolveAttribute(resId, this, true)
    }.data
}

fun Context.resolveThemedBoolean(@AttrRes resId: Int): Boolean {
    return TypedValue().apply {
        theme.resolveAttribute(resId, this, true)
    }.data != 0
}

fun Context.resolveThemedResourceId(@AttrRes resId: Int): Int {
    return TypedValue().apply {
        theme.resolveAttribute(resId, this, true)
    }.resourceId
}