package com.notes.eversimple;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.evernote.auth.EvernoteAuth;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.type.Notebook;
import com.mrgames13.jimdo.splashscreen.App.SplashScreenBuilder;
import com.stephentuso.welcome.WelcomeHelper;


public class MainActivity extends AppCompatActivity {
    public static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2048;
    private static final int REQUEST_PHONE_CALL =1234 ;

    SharedPreferences mSharedPreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean userLogedIn =mSharedPreference.getBoolean("loginstatus",false);


        initializeView();

//        if(userLogedIn){
//
//            initializeView();
//        }
//       else {
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this )) {
//
//                    //If the draw over permission is not available open the settings screen
//                    //to grant the permission.
//                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                            Uri.parse("package:" + getPackageName()));
//                    startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
//
//        } else {
//
//            initializeView();
//        }}


    }


    private void initializeView() {
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            Log.d("Everee","Got to Activity Creation");

            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                initializeView();
            } else { //Permission is not available
                Toast.makeText(this,

                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            Log.d("Everee","Got to Activity Creation ELSE PART!");


        }
    }
}
