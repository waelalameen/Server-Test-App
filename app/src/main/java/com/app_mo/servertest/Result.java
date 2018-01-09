package com.app_mo.servertest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

public class Result extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private String method;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 1234;
    private Twitter mTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        method = getIntent().getExtras().getString("method");
        String uid = getIntent().getExtras().getString("uid");
        String name = getIntent().getExtras().getString("name");
        String email = getIntent().getExtras().getString("email");
        String photoUrl = getIntent().getExtras().getString("photo_url");

        ((TextView) findViewById(R.id.user_id)).append(uid);
        ((TextView) findViewById(R.id.user_name)).append(name);
        ((TextView) findViewById(R.id.user_email)).append(email);
        ImageView userImage = (ImageView) findViewById(R.id.user_image);

        Picasso.with(this).load(photoUrl).into(userImage);

        Button logOut = (Button) findViewById(R.id.logout);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (method.equalsIgnoreCase("facebook")) {
                    if (AccessToken.getCurrentAccessToken() != null && com.facebook.Profile.getCurrentProfile() != null) {
                        LoginManager.getInstance().logOut();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                } else if (method.equalsIgnoreCase("google")) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        }
                    });
                } else {
                    TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();

                    if (session != null) {
                        CookieSyncManager.createInstance(getApplicationContext());
                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.removeSessionCookie();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorCode() + ": " + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }
}
