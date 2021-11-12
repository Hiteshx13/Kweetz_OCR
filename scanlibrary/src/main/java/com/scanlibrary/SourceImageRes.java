package com.scanlibrary;

import android.os.Parcel;
import android.os.Parcelable;

public  class SourceImageRes implements Parcelable {
    public int width;
    public int height;
    public SourceImageRes(int width,int height){
    this.width=width;
    this.height=height;
    }

    protected SourceImageRes(Parcel in) {
        width = in.readInt();
        height = in.readInt();
    }

    public static final Creator<SourceImageRes> CREATOR = new Creator<SourceImageRes>() {
        @Override
        public SourceImageRes createFromParcel(Parcel in) {
            return new SourceImageRes(in);
        }

        @Override
        public SourceImageRes[] newArray(int size) {
            return new SourceImageRes[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(width);
        dest.writeInt(height);
    }
}
