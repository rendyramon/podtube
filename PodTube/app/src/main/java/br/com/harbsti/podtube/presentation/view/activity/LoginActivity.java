package br.com.harbsti.podtube.presentation.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.f2prateek.dart.HensonNavigable;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.common.collect.Lists;

import br.com.harbsti.podtube.R;
import br.com.harbsti.podtube.presentation.helper.AuthHelper;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by marcosharbs on 23/11/16.
 */

@HensonNavigable
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleSignInOptions gso;
    private GoogleApiClient googleApiClient;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.btn_login_google)
    public void onLoginGoogle() {
        if(gso == null) {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
        }
        if(googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, 001);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 001){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                email = acct.getEmail();

                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.GET_ACCOUNTS)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.GET_ACCOUNTS},
                            001);

                }else {
                    askYoutubePermission();
                }
            }
        }else if(requestCode == 002 && resultCode == RESULT_OK){
            login();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 001: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    askYoutubePermission();
                }
                return;
            }
        }
    }

    private void login() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("USER_EMAIL", email).apply();
        startActivity(Henson.with(this).gotoHomeActivity().build());
        finish();
    }

    private void askYoutubePermission() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(LoginActivity.this, Lists.newArrayList(YouTubeScopes.YOUTUBE));
                    credential.setSelectedAccountName(email);
                    YouTube youtube = new YouTube.Builder(AuthHelper.HTTP_TRANSPORT,
                            AuthHelper.JSON_FACTORY, credential)
                            .setApplicationName("PodTube").build();
                    youtube.channels().list("snippet").setMine(true).setMaxResults(1l).execute();
                    login();
                }catch (final UserRecoverableAuthIOException e1) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            startActivityForResult(e1.getIntent(), 002);
                        }
                    });
                }catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

}
