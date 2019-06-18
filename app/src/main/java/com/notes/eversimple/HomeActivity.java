package com.notes.eversimple;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;

import java.util.ArrayList;
import java.util.List;

import static android.app.PendingIntent.getActivity;
import static android.support.v7.app.AlertDialog.*;



public class HomeActivity extends AppCompatActivity implements CreateNoteDialog.CreateNoteDialogListener {

    private ImageView mAccept;
    private ImageView mDeny;
    SharedPreferences mSharedPreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAccept=findViewById(R.id.home_accept);
        mDeny=findViewById(R.id.home_deny);

        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean FloatAccept =mSharedPreference.getBoolean("floatButtonAccept",false);

        if(FloatAccept){
            mAccept.setVisibility(View.GONE);
            mDeny.setVisibility(View.VISIBLE);

            Toast.makeText(this, "EverSimple Service is currently Active", Toast.LENGTH_SHORT).show();

        }else{
            mDeny.setVisibility(View.GONE);
            mAccept.setVisibility(View.VISIBLE);

            Toast.makeText(this, "EverSimple Service is currently inActive. Switch it on to use Eversimple Service.", Toast.LENGTH_SHORT).show();

        }

        mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mSharedPreference.edit();
                editor.putBoolean("floatButtonAccept", true);
                editor.apply();
                mAccept.setVisibility(View.GONE);
                mDeny.setVisibility(View.VISIBLE);
                Log.d("Everee","Accept Clicked");
                createNoteBookMe();

            }
        });
        mDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mSharedPreference.edit();
                editor.putBoolean("floatButtonAccept", false);
                editor.apply();
                mDeny.setVisibility(View.GONE);
                mAccept.setVisibility(View.VISIBLE);
                Log.d("Everee","Deny Clicked");

            }
        });



        findViewById(R.id.home_see_notes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if (!EvernoteSession.getInstance().isLoggedIn()) {
                    return;
                }

                EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
                noteStoreClient.listNotebooksAsync(new EvernoteCallback<List<Notebook>>() {
                    @Override
                    public void onSuccess(List<Notebook> result) {
                        List<String> namesList = new ArrayList<>(result.size());
                        for (Notebook notebook : result) {
                            namesList.add(notebook.getName());
                        }
                        String notebookNames = TextUtils.join(", ", namesList);
                        Toast.makeText(getApplicationContext(), notebookNames + " notebooks have been retrieved", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onException(Exception exception) {
                        Log.e("EverSimple", "Error retrieving notebooks", exception);
                    }
                });
            }
        });


    }

    private void createNoteBookMe() {
        if (!EvernoteSession.getInstance().isLoggedIn()) {
            return;
        }

        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();

        Notebook notebook = new Notebook();
        notebook.setName("PhoneMemo");
        notebook.setGuid("phonememo_notebook_guid");


        noteStoreClient.createNotebookAsync(notebook, new EvernoteCallback<Notebook>() {
            @Override
            public void onSuccess(Notebook result) {
                        Log.d("Everee","NB CR");
            }

            @Override
            public void onException(Exception exception) {
                Log.d("Everee","NB CRere "+exception);
            }
        });
    }
    @Override
    public void applyTexts(String title) {
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();

    }
}
