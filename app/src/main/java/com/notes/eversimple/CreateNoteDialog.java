package com.notes.eversimple;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.type.Note;


public class CreateNoteDialog extends AppCompatDialogFragment {
    private EditText editTitle;
    private EditText editContent;
    CreateNoteDialogListener listener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_notes, null);

        editTitle = view.findViewById(R.id.createnote_title);
        editContent = view.findViewById(R.id.createnote_content);


        builder.setView(view)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Add Note", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String title = editTitle.getText().toString();
                        final String content = editContent.getText().toString();
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
                                listener.applyTexts("Note Added Successfully!");

                            }

                            @Override
                            public void onException(Exception exception) {
                                Log.e("EverSimplee", "Error creating note", exception);
                                listener.applyTexts("Error Occurred!");
                            }
                        });
                    }
                });


        return builder.create();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (CreateNoteDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }
    public interface CreateNoteDialogListener {
        void applyTexts(String text);
    }


}