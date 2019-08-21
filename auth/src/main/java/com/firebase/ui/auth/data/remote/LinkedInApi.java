package com.firebase.ui.auth.data.remote;

import androidx.annotation.RestrictTo;

import com.firebase.ui.auth.data.model.LinkedInFirebaseRequest;
import com.firebase.ui.auth.data.model.LinkedInFirebaseResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public interface LinkedInApi {

    @POST("auth/linkedin/token")
    Call<LinkedInFirebaseResponse> createFirebaseToken(
            @Body LinkedInFirebaseRequest request);
}
