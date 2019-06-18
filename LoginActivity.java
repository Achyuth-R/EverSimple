package com.notes.eversimple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.login.EvernoteLoginFragment;


public class LoginActivity extends AppCompatActivity implements EvernoteLoginFragment.ResultCallback {

    private EvernoteSession mEvernoteSession;
    private static final String CONSUMER_KEY = "iamgokul2102";
    private static final String CONSUMER_SECRET = "9e47b47db8730832";
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteServicee.SANDBOX;
    SharedPreferences mSharedPreference;

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main));

        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean userLogedIn =mSharedPreference.getBoolean("loginstatus",false);

        if(userLogedIn){
            Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitleTextColor(getResources().getColor(R.color.tb_text));
//
//        setSupportActionBar(toolbar);
        mEvernoteSession = new EvernoteSession.Builder(this)
                .setEvernoteService(EVERNOTE_SERVICE)
                .build(CONSUMER_KEY, CONSUMER_SECRET)
                .asSingleton();

        mButton = (Button) findViewById(R.id.button_login);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EvernoteSession.getInstance().authenticate(LoginActivity.this);
                mButton.setEnabled(false);
            }
        });

    }

    @Override
    public void onLoginFinished(boolean successful) {
        if (successful) {
            finish();
        } else {
            mButton.setEnabled(true);
        }
    }
}
