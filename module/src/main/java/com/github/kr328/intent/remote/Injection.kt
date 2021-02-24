package com.github.kr328.intent.remote

import android.os.Parcel
import android.os.Parcelable

data class Injection(val packageName: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(packageName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Injection> {
        override fun createFromParcel(parcel: Parcel): Injection {
            return Injection(parcel)
        }

        override fun newArray(size: Int): Array<Injection?> {
            return arrayOfNulls(size)
        }
    }
}
