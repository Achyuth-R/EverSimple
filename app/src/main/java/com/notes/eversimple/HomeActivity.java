package com.notes.eversimple;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.app.PendingIntent.getActivity;
import static android.support.v7.app.AlertDialog.*;



public class HomeActivity extends AppCompatActivity implements CreateNoteDialog.CreateNoteDialogListener {

    private ImageView mAccept;
    private ImageView mDeny;
    SharedPreferences mSharedPreference;
    public final String noteBookName="PhoneMemo(Notebooks created on Call)";


    private int PHONE_PERMISSION_CODE = 1,CONTACT_PERMISSION_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAccept=findViewById(R.id.home_accept);
        mDeny=findViewById(R.id.home_deny);

        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean FloatAccept =mSharedPreference.getBoolean("floatButtonAccept",false);
        String notenum =mSharedPreference.getString("notebookGUID","none");
        Log.d("Everee","Recorded in Hime "+ notenum);


        if (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
            requestPhonePermission();


        checkNoteBooks();

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
             if (ContextCompat.checkSelfPermission(HomeActivity.this,
                        Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                    SharedPreferences.Editor editor = mSharedPreference.edit();
                editor.putBoolean("floatButtonAccept", true);
                editor.apply();
                mAccept.setVisibility(View.GONE);
                mDeny.setVisibility(View.VISIBLE);
                Log.d("Everee","Accept Clicked");}
                else{

                    requestContactPermission();

                }


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







    }
    public void checkNoteBooks(){

        if (!EvernoteSession.getInstance().isLoggedIn()) {
            Toast.makeText(this, "Authorization Error!", Toast.LENGTH_SHORT).show();
        }

        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
        noteStoreClient.listNotebooksAsync(new EvernoteCallback<List<Notebook>>() {
            boolean notebookfound=false;
            @Override
            public void onSuccess(List<Notebook> result) {
                List<String> namesList = new ArrayList<>(result.size());
                for (Notebook notebook : result) {
                    namesList.add(notebook.getName());
                    if(notebook.getName().equals(noteBookName)){
                        Toast.makeText(HomeActivity.this, "All your notes will be saved in "+noteBookName, Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = mSharedPreference.edit();
                        editor.putString("notebookGUID", notebook.getGuid());
                        editor.apply();
                        Log.d("Everee","List note guid "+notebook.getGuid());
                        notebookfound=true;
                    }
                }
                if(!notebookfound)
                    createNoteBookMe();
                String notebookNames = TextUtils.join(", ", namesList);
                Log.d("Everee",notebookNames);
                Toast.makeText(getApplicationContext(), notebookNames + " notebooks have been retrieved", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onException(Exception exception) {
                Log.e("EverSimple", "Error retrieving notebooks", exception);
            }
        });
    }

    private void createNoteBookMe() {
        if (!EvernoteSession.getInstance().isLoggedIn()) {
            return;
        }

        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
        List<String> tags = new LinkedList<>();
        tags.add("My Number");
        final Notebook notebookcr = new Notebook();
        notebookcr.setName(noteBookName);
        NoteFilter filter = new NoteFilter();
        filter.setTagGuids(tags);



        noteStoreClient.createNotebookAsync(notebookcr, new EvernoteCallback<Notebook>() {
            @Override
            public void onSuccess(Notebook result) {
                Log.d("Everee","NB CR");
                Toast.makeText(HomeActivity.this, "A new notebook with name "+noteBookName+" has been created", Toast.LENGTH_SHORT).show();
                Toast.makeText(HomeActivity.this, "All your notes will be saved in "+noteBookName, Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = mSharedPreference.edit();
                editor.putString("notebookGUID", notebookcr.getGuid());
                editor.apply();
                editor.commit();
                Log.d("Everee","Create note guid "+notebookcr.getGuid());
            }

            @Override
            public void onException(Exception exception) {
                Log.d("Everee","NB CRere "+exception);
                Toast.makeText(HomeActivity.this, "An error occured creating a notebook Try again later!", Toast.LENGTH_SHORT).show();

            }
        });
    }
    @Override
    public void applyTexts(String title) {
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();

    }

    private void requestPhonePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Phone Permission Needed")
                    .setMessage("PhoneMemo can read Call state only using phone permission.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(HomeActivity.this,
                                    new String[] {Manifest.permission.READ_PHONE_STATE}, PHONE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_PHONE_STATE}, PHONE_PERMISSION_CODE);
        }
    }
    private void requestContactPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CONTACTS)) {

            new AlertDialog.Builder(this)
                    .setTitle("Contact Permission Needed")
                    .setMessage("Only using contact permission PhoneMemo can create notes with caller's name")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(HomeActivity.this,
                                    new String[] {Manifest.permission.READ_CONTACTS}, CONTACT_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_CONTACTS}, CONTACT_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PHONE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CONTACT_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

