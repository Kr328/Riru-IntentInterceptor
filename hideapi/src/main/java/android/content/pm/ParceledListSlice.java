package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import utils.Utils;

public class ParceledListSlice<T extends Parcelable> extends BaseParceledListSlice<T> {
    public static final Creator<ParceledListSlice<?>> CREATOR = new Creator<ParceledListSlice<?>>() {
        @Override
        public ParceledListSlice<?> createFromParcel(Parcel source) {
            return Utils.throwStub();
        }

        @Override
        public ParceledListSlice<?>[] newArray(int size) {
            return Utils.throwStub();
        }
    };

    public ParceledListSlice(List<T> list) {
        super(list);
    }

    @Override
    public int describeContents() {
        throw new IllegalArgumentException("Stub!");
    }
}
