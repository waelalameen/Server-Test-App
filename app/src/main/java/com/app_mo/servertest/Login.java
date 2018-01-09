package com.app_mo.servertest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;

import retrofit2.Call;

public class Login extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private TwitterAuthClient mTwitterAuthClient;
    private static final int RC_SIGN_IN = 1234;
    private String uuid, name, email, photoUrl, userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        Twitter.initialize(this);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mTwitterAuthClient = new TwitterAuthClient();

        ImageButton fbButton = (ImageButton) findViewById(R.id.facebook_button);
        ImageButton googleButton = (ImageButton) findViewById(R.id.google_button);
        ImageButton twitterButton = (ImageButton) findViewById(R.id.twitter_button);

        fbButton.setOnClickListener(this);
        googleButton.setOnClickListener(this);
        twitterButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.facebook_button:
                signInWithFacebook();
//                if (AccessToken.getCurrentAccessToken() != null && com.facebook.Profile.getCurrentProfile() != null) {
//                    LoginManager.getInstance().logOut();
//                    Log.d("logged out", "yes");
//                }
                break;
            case R.id.google_button:
                signInWithGoogle();
                break;
            case R.id.twitter_button:
                signInWithTwitter();
                break;
            default:
                break;
        }
    }

    private void signInWithTwitter() {
        mTwitterAuthClient.authorize(this, new Callback<TwitterSession>() {
            @Override
            public void success(com.twitter.sdk.android.core.Result<TwitterSession> result) {
                Log.i("UserId", String.valueOf(result.data.getUserId()));
                Log.i("UserName", result.data.getUserName());
                uuid = String.valueOf(result.data.getUserId());
                userName = result.data.getUserName();

                Call<User> user = TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(false, false, false);
                user.enqueue(new Callback<User>() {

                    @Override
                    public void success(com.twitter.sdk.android.core.Result<User> result) {
                        Intent intent = new Intent(getApplicationContext(), Result.class);
                        intent.putExtra("method", "twitter");
                        intent.putExtra("uid", uuid);
                        intent.putExtra("name", result.data.name);
                        intent.putExtra("email", userName);
                        intent.putExtra("photo_url", result.data.profileImageUrl);
                        startActivity(intent);
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_photos", "email", "public_profile",
                "user_posts"));
        LoginManager.getInstance().logInWithPublishPermissions(this, Collections.singletonList("publish_actions"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("uid", loginResult.getAccessToken().getUserId());
                Log.i("token", loginResult.getAccessToken().getToken());
                uuid = loginResult.getAccessToken().getUserId();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("GraphResponse", response.toString());

                        try {
                            JSONObject jsonObj = new JSONObject(String.valueOf(response.getJSONObject()));
                            name = jsonObj.getString("name");
                            email = jsonObj.getString("email");
                            photoUrl = jsonObj.getJSONObject("picture")
                                    .getJSONObject("data")
                                    .getString("url");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(getApplicationContext(), Result.class);
                        intent.putExtra("method", "facebook");
                        intent.putExtra("uid", uuid);
                        intent.putExtra("name", name);
                        intent.putExtra("email", email);
                        intent.putExtra("photo_url", photoUrl);
                        startActivity(intent);
                    }
                });

                Bundle params = new Bundle();
                params.putString("fields", "id, name, email, picture");
                request.setParameters(params);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.i("Login Status", "Login Cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("FacebookException", error.getMessage());
            }
        });
    }

    public String convertStandardJSONString(String data_json){
        data_json = data_json.replace("\\", "");
        data_json = data_json.replace("\"{", "{");
        data_json = data_json.replace("}\",", "},");
        data_json = data_json.replace("}\"", "}");
        return data_json;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }

        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Log.d("handleSignInResult:", String.valueOf(result.isSuccess()));

        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            Intent intent = new Intent(getApplicationContext(), Result.class);
            intent.putExtra("method", "google");
            intent.putExtra("uid", acct.getId());
            intent.putExtra("name", acct.getDisplayName());
            intent.putExtra("email", acct.getEmail());
            intent.putExtra("photo_url", acct.getPhotoUrl().toString());
            Log.d("photo_url", acct.getPhotoUrl().toString());
            startActivity(intent);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorCode() + ": " + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }
}
