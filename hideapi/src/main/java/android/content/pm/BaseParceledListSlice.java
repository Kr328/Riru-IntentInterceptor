package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import utils.Utils;

abstract class BaseParceledListSlice<T> implements Parcelable {
    public BaseParceledListSlice(List<T> list) {
        Utils.throwStub();
    }

    public List<T> getList() {
        return Utils.throwStub();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Utils.throwStub();
    }
}