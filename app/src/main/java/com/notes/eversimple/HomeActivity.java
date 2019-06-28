package com.notes.eversimple;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.evernote.auth.EvernoteService;
import com.evernote.client.android.AuthenticationResult;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteClientFactory;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.client.android.asyncclient.EvernoteUserStoreClient;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.User;
import com.evernote.thrift.TException;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.app.PendingIntent.getActivity;
import static com.notes.eversimple.LoginActivity.mEvernoteSession;
import static com.notes.eversimple.MainActivity.CODE_DRAW_OVER_OTHER_APP_PERMISSION;


public class HomeActivity extends AppCompatActivity  {


    private static final int REQUEST_CODE_ASK_PERMISSIONS = 3;
    SharedPreferences mSharedPreference;
    public final String noteBookName="PhoneMemo(Notebooks created on Call)";
    ImageView viewOn,phone,contact,setting;
    TextView mText;
    String name;


    private int PHONE_PERMISSION_CODE = 1,CONTACT_PERMISSION_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        final LabeledSwitch labeledSwitch = findViewById(R.id.myswitch);

        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean FloatAccept =mSharedPreference.getBoolean("floatButtonAccept",false);
        String notenum =mSharedPreference.getString("notebookGUID","none");
        Log.d("Everee","Recorded in Hime "+ notenum);

        viewOn=findViewById(R.id.permViewOnTop);
        phone=findViewById(R.id.phonePermission);
        contact=findViewById(R.id.contactPermission);
//        setting=findViewById(R.id.home_menu);
        mText=findViewById(R.id.name);



//        setting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomeActivity.this,SettingActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        checkNoteBooks();
        labeledSwitch.setColorDisabled(R.color.evernoteoff);



        labeledSwitch.setLabelOn("Active");
        labeledSwitch.setLabelOff("Disabled");

        if(FloatAccept){

            labeledSwitch.setOn(true);



            Toast.makeText(this, "EverSimple Service is currently Active", Toast.LENGTH_SHORT).show();

        }else{
            labeledSwitch.setColorBorder(R.color.evernoteoff);
            labeledSwitch.setOn(false);

            Toast.makeText(this, "EverSimple Service is currently inActive. Switch it on to use Eversimple Service.", Toast.LENGTH_SHORT).show();

        }


        labeledSwitch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn&&checkCallPermission()){
                    if (ContextCompat.checkSelfPermission(HomeActivity.this,
                            Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                        requestContactPermission();
                        labeledSwitch.setOn(false);
                    }
                    else if (ContextCompat.checkSelfPermission(HomeActivity.this,
                            Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
                        requestPhonePermission();
                        labeledSwitch.setOn(false);
                    }
                    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(HomeActivity.this )) {

                        //If the draw over permission is not available open the settings screen
                        //to grant the permission.
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
                        labeledSwitch.setOn(false);

                    }
                    else{
                        SharedPreferences.Editor editor = mSharedPreference.edit();
                        editor.putBoolean("floatButtonAccept", true);
                        editor.apply();
                        labeledSwitch.setOn(true);


                        Log.d("Everee","Accept Clicked");


                    }
                }
                else{
                    SharedPreferences.Editor editor = mSharedPreference.edit();
                    editor.putBoolean("floatButtonAccept", false);
                    editor.apply();
                    labeledSwitch.setOn(false);


                    Log.d("Everee","Deny Clicked");
                }

            }
        });







        viewOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(HomeActivity.this )) {

                    //If the draw over permission is not available open the settings screen
                    //to grant the permission.
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);

                } else {
                    Toast.makeText(HomeActivity.this, "View on Top permission is already Given", Toast.LENGTH_SHORT).show();
                }

            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    requestPhonePermission();

            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    requestContactPermission();

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
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this )) {

            viewOn.setImageResource(R.drawable.ic_close_black);
        } else {
            viewOn.setImageResource(R.drawable.ic_check_black);
        }


        if (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            requestPhonePermission();
        else if (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            requestContactPermission();



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PHONE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                phone.setImageResource(R.drawable.ic_check_black);
                Toast.makeText(this, "Phone Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                phone.setImageResource(R.drawable.ic_close_black);

            }
        }
        if (requestCode == CONTACT_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                contact.setImageResource(R.drawable.ic_check_black);
                Toast.makeText(this, "Contact Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {

                contact.setImageResource(R.drawable.ic_close_black);
            }
        }
    }

    private boolean checkCallPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            String call = Manifest.permission.CALL_PHONE;
            String outgoing = Manifest.permission.PROCESS_OUTGOING_CALLS;
            String incoming = Manifest.permission.READ_PHONE_STATE;
            int hasCallPermission = checkSelfPermission(call);
            List<String> permissions = new ArrayList<String>();
            if (hasCallPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(call);
                permissions.add(outgoing);
                permissions.add(incoming);
            }
            if (!permissions.isEmpty()) {
                String[] params = permissions.toArray(new String[permissions.size()]);
                requestPermissions(params, REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}

