package com.notes.eversimple;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.type.Note;

public class WriteNoteAvtivity extends AppCompatActivity {
    private EditText mtitle,mcontent;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_note_avtivity);
        mtitle=findViewById(R.id.WAlink);
        mcontent=findViewById(R.id.WAannouncement);

        findViewById(R.id.WAback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        findViewById(R.id.WAsend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title=mtitle.getText().toString();
                String content=mcontent.getText().toString();
                if(title.equals("")||content.equals("")){
                    Toast.makeText(WriteNoteAvtivity.this, "Title or Content cannot be empty", Toast.LENGTH_SHORT).show();
                }else{
                    if (!EvernoteSession.getInstance().isLoggedIn()) {
                        return;
                    }


                    EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();

                    Note note = new Note();
                    note.setTitle(title);
                    note.setContent(EvernoteUtil.NOTE_PREFIX + content + EvernoteUtil.NOTE_SUFFIX);

                    noteStoreClient.createNoteAsync(note, new EvernoteCallback<Note>() {
                        @Override
                        public void onSuccess(Note result) {
                            Log.e("EverSimplee", "Created note");
                            Toast.makeText(WriteNoteAvtivity.this, "Successfully created Note "+title+" ! ", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                            finish();


                        }

                        @Override
                        public void onException(Exception exception) {
                            Log.e("EverSimplee", "Error creating note", exception);
                            Toast.makeText(WriteNoteAvtivity.this, "Oops! an error occured", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            }
        });

    }
}
