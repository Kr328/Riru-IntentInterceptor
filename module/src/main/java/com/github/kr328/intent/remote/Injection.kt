package com.github.kr328.intent.remote

import android.os.Parcel
import android.os.Parcelable
import android.os.SharedMemory
import com.github.kr328.intent.util.readList
import com.github.kr328.intent.util.writeToParcel

data class Injection(val classes: List<SharedMemory>, val interceptor: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readList(SharedMemory.CREATOR),
        parcel.readString()!!,
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        classes.writeToParcel(parcel, flags)
        interceptor.writeToParcel(parcel)
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
