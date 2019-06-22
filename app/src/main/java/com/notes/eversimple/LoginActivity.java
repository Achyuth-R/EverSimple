package com.notes.eversimple;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.login.EvernoteLoginFragment;


public class LoginActivity extends AppCompatActivity implements EvernoteLoginFragment.ResultCallback {
    public static EvernoteSession mEvernoteSession;
    private static final String CONSUMER_KEY = "eversimpleadmin";
    private static final String CONSUMER_SECRET = "d7bd6647c51232cf";
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.PRODUCTION;
    public static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;
    SharedPreferences mSharedPreference;


    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean userLogedIn =mSharedPreference.getBoolean("loginstatus",false);







        mEvernoteSession = new EvernoteSession.Builder(this)
                .setEvernoteService(EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                .build(CONSUMER_KEY, CONSUMER_SECRET)
                .asSingleton();

        if(mEvernoteSession.isLoggedIn()){
            Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }


        mButton = (Button) findViewById(R.id.notify_me2);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mEvernoteSession.authenticate(LoginActivity.this);

                mButton.setEnabled(false);
            }
        });

    }

    @Override
    public void onLoginFinished(boolean successful) {
        if (successful) {
            SharedPreferences.Editor editor = mSharedPreference.edit();
            editor.putBoolean("loginstatus", true);
            editor.apply();
            mEvernoteSession.getAuthToken();

            Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Login Error", Toast.LENGTH_SHORT).show();
            mButton.setEnabled(true);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EvernoteSession.REQUEST_CODE_LOGIN:
                if (resultCode == Activity.RESULT_OK) {
//                   // Log.d("Everee",data);
//                    SharedPreferences.Editor editor = mSharedPreference.edit();
//                    editor.putBoolean("loginstatus", true);
//                    editor.apply();
                    // handle success
                   // Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                    startActivity(data);
                    finish();
                } else {
                    Toast.makeText(this, "Login Error", Toast.LENGTH_SHORT).show();
                    mButton.setEnabled(true);
                    // handle failure
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
