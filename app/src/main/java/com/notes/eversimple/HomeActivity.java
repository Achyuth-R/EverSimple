package com.notes.eversimple;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAccept=findViewById(R.id.home_accept);
        mDeny=findViewById(R.id.home_deny);


        mDeny.setVisibility(View.GONE);
        mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccept.setVisibility(View.GONE);
                mDeny.setVisibility(View.VISIBLE);
                Intent intent = new Intent(HomeActivity.this,FloatingViewService.class);
                startService(intent);
                finish();
            }
        });
        mDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeny.setVisibility(View.GONE);
                mAccept.setVisibility(View.VISIBLE);

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

        findViewById(R.id.home_create_notes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if (!EvernoteSession.getInstance().isLoggedIn()) {
                    Toast.makeText(HomeActivity.this, "Login Error!", Toast.LENGTH_SHORT).show();
                    return;
                }
                openDialog();
            }
        });

    }
    public void openDialog() {
        CreateNoteDialog createNoteDialog = new CreateNoteDialog();
        createNoteDialog.show(getSupportFragmentManager(), "create note dialog");
    }


    @Override
    public void applyTexts(String title) {
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();

    }
}
