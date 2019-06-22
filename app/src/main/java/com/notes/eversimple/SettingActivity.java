package com.notes.eversimple;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;

public class SettingActivity extends AppCompatActivity {
    SharedPreferences mSharedPreference;
    private RadioGroup radioPositiongrp,radioColorgrp;
    private RadioButton radioPosButton1,radioPosButton2,radioPosButton3,radioColorButton1,radioColorButton2,radioColorButton3;
    private ImageView btnBack;
    private Button mSave;
    private TextView mTextview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);

        radioPositiongrp = findViewById(R.id.radioGroup);
        radioColorgrp = findViewById(R.id.radioColorGroup);

        btnBack = findViewById(R.id.back);
        mTextview=findViewById(R.id.name);

        if(EvernoteSession.getInstance().isLoggedIn()){
            mTextview.setText("Click here to Logout");
        }
        else{
            mTextview.setText("Reset App");
        }

        mTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EvernoteSession.getInstance().logOut();
                SharedPreferences mSharedPreference =getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mSharedPreference.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(SettingActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        radioPosButton1 = (RadioButton) findViewById(R.id.radioButton);
        radioPosButton2 = (RadioButton) findViewById(R.id.radioButton2);
        radioPosButton3 = (RadioButton) findViewById(R.id.radioButton3);

        radioColorButton1 = (RadioButton) findViewById(R.id.radioColorWhite);
        radioColorButton2 = (RadioButton) findViewById(R.id.radioColorGrey);
        radioColorButton3 = (RadioButton) findViewById(R.id.radioColorBlack);

//getting strings from saved settings
        int position = mSharedPreference.getInt("position", radioPosButton2.getId());
        int color = mSharedPreference.getInt("color", radioColorButton2.getId());

        radioPositiongrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int selectedId) {
                if (selectedId == radioPosButton1.getId()) {
                    SharedPreferences.Editor editor = mSharedPreference.edit();
                    editor.putInt("pmPos", 1);
                    editor.putInt("position", radioPosButton1.getId());
                    editor.apply();



                } else if (selectedId == radioPosButton2.getId()) {

                    SharedPreferences.Editor editor = mSharedPreference.edit();
                    editor.putInt("pmPos", 2);
                    editor.putInt("position", radioPosButton2.getId());
                    editor.apply();



                } else {
                    SharedPreferences.Editor editor = mSharedPreference.edit();
                    editor.putInt("pmPos", 3);
                    editor.putInt("position", radioPosButton3.getId());
                    editor.apply();
                    Log.d("Everee","Bottom Selected");

                }
            }
        });

        radioColorgrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int selectIdColor) {
                if (selectIdColor == radioColorButton1.getId()) {
                    SharedPreferences.Editor editor = mSharedPreference.edit();
                    editor.putInt("pmColor", 1);
                    editor.putInt("color", radioColorButton1.getId());
                    editor.apply();

                } else if (selectIdColor == radioPosButton2.getId()) {
                    SharedPreferences.Editor editor = mSharedPreference.edit();
                    editor.putInt("pmColor", 2);
                    editor.putInt("color", radioColorButton2.getId());
                    editor.apply();

                } else {
                    SharedPreferences.Editor editor = mSharedPreference.edit();
                    editor.putInt("pmColor", 3);
                    editor.putInt("color", radioColorButton3.getId());
                    editor.apply();

                }
            }
        });

        radioPositiongrp.check(position);
        radioColorgrp.check(color);
    }
}
