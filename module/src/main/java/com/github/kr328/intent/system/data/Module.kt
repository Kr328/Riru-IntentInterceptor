package com.github.kr328.intent.system.data

import android.content.pm.PackageInfo
import com.github.kr328.intent.compat.appMetaData
import com.github.kr328.intent.shared.Constants
import com.github.kr328.intent.system.model.Module

private val REGEX_PACKAGE_SPLIT = Regex("[^0-9a-zA-Z_.]+")

fun Module(packageInfo: PackageInfo): Module {
    val packageName = packageInfo.packageName
    val interceptor = packageInfo.appMetaData
        .getString(Constants.MODULE_METADATA_INTERCEPTOR, "")
    val target = packageInfo.appMetaData
        .getString(Constants.MODULE_METADATA_TARGET, "").split(REGEX_PACKAGE_SPLIT)

    return Module(packageName, interceptor, target)
}