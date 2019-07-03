package com.firebase.ui.auth.data.remote;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.R;
import com.firebase.ui.auth.data.model.LinkedInAuthProvider;
import com.firebase.ui.auth.data.model.LinkedInFirebaseRequest;
import com.firebase.ui.auth.data.model.LinkedInFirebaseResponse;
import com.firebase.ui.auth.data.model.Resource;
import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.auth.ui.HelperActivityBase;
import com.firebase.ui.auth.viewmodel.ProviderSignInBase;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class LinkedInSignInHandler extends ProviderSignInBase<Void> {

    private static final int RC_LINKED_IN = 9076;

    static String LINKED_IN_OAUTH_URL = "https://www.linkedin.com/oauth/v2/authorization";
    static String LINKED_IN_TOKEN_URL = "https://www.linkedin.com/oauth/v2/accessToken";

    private AuthState mLinkedInAuthState;
    private AuthorizationService mAuthorizationService;

    private Retrofit mRetrofit;

    public LinkedInSignInHandler(Application application) {
        super(application);
        mAuthorizationService = new AuthorizationService(application);

        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://wonder-api.herokuapp.com")
                .client(getLinkedInTimeoutClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public OkHttpClient getLinkedInTimeoutClient() {

        return new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .build();
    }

    @NonNull
    public AuthorizationRequest buildLinkedInAuthorizationRequest() {

        AuthorizationServiceConfiguration serviceConfig = buildLinkedInAuthServiceConfig();

        AuthorizationRequest.Builder authRequestBuilder =
                new AuthorizationRequest.Builder(
                        serviceConfig, // the authorization service configuration
                        getApplication().getString(R.string.linkedin_client_id), // the client ID, typically pre-registered and static
                        ResponseTypeValues.CODE, // the response_type value: we want a code
                        Uri.parse(getApplication().getString(R.string.linkedin_redirect_uri))); // the redirect URI to which the auth response is sent

        authRequestBuilder.setCodeVerifier(null);

        mLinkedInAuthState = new AuthState(serviceConfig);

        return authRequestBuilder
                .setScope("r_basicprofile r_emailaddress")
                .build();
    }

    @NonNull
    private AuthorizationServiceConfiguration buildLinkedInAuthServiceConfig() {
        return new AuthorizationServiceConfiguration(
                Uri.parse(LINKED_IN_OAUTH_URL), // authorization endpoint
                Uri.parse(LINKED_IN_TOKEN_URL));
    }

    @Override
    public void startSignIn(@NonNull HelperActivityBase activity) {
        Intent intent = mAuthorizationService
                .getAuthorizationRequestIntent(buildLinkedInAuthorizationRequest());
        activity.startActivityForResult(intent, RC_LINKED_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (RC_LINKED_IN == requestCode && data != null) {

            final AuthorizationResponse resp = AuthorizationResponse.fromIntent(data);
            AuthorizationException ex = AuthorizationException.fromIntent(data);

            mLinkedInAuthState.update(resp, ex);
            setResult(Resource.<IdpResponse>forLoading());

            if (ex != null) {
                setResult(Resource.<IdpResponse>forFailure(new FirebaseUiException(
                        ErrorCodes.PROVIDER_ERROR, ex)));
                return;
            }

            if (resp == null) {
                setResult(Resource.<IdpResponse>forFailure(new RuntimeException("LinkedIn response is unsuccessful")));
                return;
            }

            mRetrofit.create(LinkedInApi.class).createFirebaseToken(
                    new LinkedInFirebaseRequest(resp.authorizationCode, resp.state))
                    .enqueue(new Callback<LinkedInFirebaseResponse>() {
                        @Override
                        public void onResponse(Call<LinkedInFirebaseResponse> call,
                                               Response<LinkedInFirebaseResponse> response) {
                            if (response.isSuccessful()) {
                                setResult(Resource.forSuccess(createResponse(
                                        response.body().getToken(),
                                        resp.state)));
                            } else {
                                setResult(Resource.<IdpResponse>forFailure(new RuntimeException("LinkedIn response is unsuccessful")));
                            }
                        }

                        @Override
                        public void onFailure(Call<LinkedInFirebaseResponse> call, Throwable t) {
                            setResult(Resource.<IdpResponse>forFailure(new FirebaseUiException(
                                    ErrorCodes.PROVIDER_ERROR, t)));
                        }
                    });
        }
    }

    private static IdpResponse createResponse(String code, String state) {
        return new IdpResponse.Builder(
                new User.Builder(LinkedInAuthProvider.PROVIDER_ID, null).build())
                .setToken(code)
                .setSecret(state)
                .setNewUser(true)
                .build();
    }
}
