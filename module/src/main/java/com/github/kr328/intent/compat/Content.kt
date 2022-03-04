package com.github.kr328.intent.compat

import android.content.ContentProviderHidden
import android.net.Uri

fun Uri.withUserId(userId: Int): Uri {
    return ContentProviderHidden.createContentUriForUser(this, userId.userHandle)
}