package com.firebase.ui.auth.data.model;

import android.support.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class LinkedInFirebaseRequest {
    private String code;
    private String state;

    public LinkedInFirebaseRequest(String code, String state) {
        this.code = code;
        this.state = state;
    }
}
