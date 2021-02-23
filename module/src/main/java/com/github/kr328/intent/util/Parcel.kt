package com.github.kr328.intent.util

import android.os.Parcel
import android.os.Parcelable

fun String.writeToParcel(parcel: Parcel) {
    parcel.writeString(this)
}

fun <T> Parcel.readList(creator: Parcelable.Creator<T>): List<T> {
    return List(readInt()) { creator.createFromParcel(this) }
}

fun List<Parcelable>.writeToParcel(parcel: Parcel, flag: Int) {
    parcel.writeInt(size)

    forEach {
        it.writeToParcel(parcel, flag)
    }
}

