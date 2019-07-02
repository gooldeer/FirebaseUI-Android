package com.firebase.ui.auth.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class LinkedInFirebaseResponse implements Parcelable {

    public static final Creator<LinkedInFirebaseResponse> CREATOR = new Creator<LinkedInFirebaseResponse>() {
        @Override
        public LinkedInFirebaseResponse createFromParcel(Parcel in) {
            return new LinkedInFirebaseResponse(in);
        }

        @Override
        public LinkedInFirebaseResponse[] newArray(int size) {
            return new LinkedInFirebaseResponse[size];
        }
    };
    private String token;

    public LinkedInFirebaseResponse() {
    }

    protected LinkedInFirebaseResponse(Parcel in) {
        token = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(token);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getToken() {
        return token;
    }
}
