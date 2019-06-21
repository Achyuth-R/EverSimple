package com.notes.eversimple;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.View;


import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;



public class IntroActivity extends MyIntro {
    SharedPreferences mSharedPreference;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean userLogedIn =mSharedPreference.getBoolean("loginstatus",false);






        if (userLogedIn) {

                    Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }


        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.white)
                .buttonsColor(R.color.colorPrimary)
                .image(R.drawable.logobig)
                .title("PhoneMemo")
                .description("Create notes on call using Evernote")
                .build()
        );
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.white)
                .buttonsColor(R.color.colorPrimary)
                .image(R.drawable.caller)
                .title("Elegant Experience")
                .description("Slide Left to create new note and right to search for notes")
                .build()
        );
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.white)
                .buttonsColor(R.color.colorPrimary)
                .image(R.drawable.contactsearch)
                .title("Instantaneous")
                .description("Quickly create note for that particular contact")
                .build()
        );
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.white)
                .buttonsColor(R.color.colorPrimary)
                .image(R.drawable.bookmark)
                .title("Well Organized")
                .description("Notes are well organized and can be instantly searched with contact's name")
                .build()
        );
        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.colorPrimary)
                        .buttonsColor(R.color.colorPrimary)
                        .image(R.drawable.inonenotebook)
                        .title("One Notebook")
                        .description("Find all your notes on PhoneMemo Notebook")
                        .build(),

                new MessageButtonBehaviour(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent loginAction = new Intent(IntroActivity.this, MainActivity.class);
                        startActivity(loginAction);
                        finish();
                    }
                }, "Get Started!"));
    }
}