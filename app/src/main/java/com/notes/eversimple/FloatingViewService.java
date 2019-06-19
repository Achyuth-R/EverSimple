package com.notes.eversimple;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.client.android.asyncclient.EvernoteUserStoreClient;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;
import com.evernote.edam.type.Notebook;

import java.net.URISyntaxException;
import java.util.*;

import static android.content.Intent.getIntent;
import static android.content.Intent.getIntentOld;


public class FloatingViewService extends Service{
    public static String callPhoneNumber;

    private WindowManager mWindowManager;
    private View mFloatingView;
    SharedPreferences mSharedPreference;
    private static final String TAG_NAME_LIST  = "TAG_NAME_LIST";
    private static final String NOTEBOOK_GUID="NOTEBOOK_GUID";
    public static final String QUERY="QUERY";

    public final static String mynumber="My Number";

    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startMyOwnForeground();
        }
        else{
            startForeground(1, new Notification());
        }


        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);


        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        //Add the view to the window.
//        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY ,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                PixelFormat.TRANSLUCENT);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);







        //Specify the view position
        params.gravity = Gravity.START | Gravity.END;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;
        mSharedPreference = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(this);
        final Boolean FloatAccept =mSharedPreference.getBoolean("floatButtonAccept",false);
        final String getnotenum =mSharedPreference.getString("notebookGUID","none");
        //Add the view to the window
        if(FloatAccept){
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mFloatingView, params);
            Log.d("Everee","Open Float");
        }

        //The root element of the collapsed view layout
        final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);

        mFloatingView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });





        //When user clicks on the image view of the collapsed layout,
        //visibility of the collapsed layout will be changed to "View.GONE"
        //and expanded view will become visible.
        Log.d("Everee","Phn Num Is : "+ callPhoneNumber);
        final ArrayList<String> tags=new ArrayList<String>();
        tags.add("Eversimple");
        Log.d("Everee","Name: "+getContactName(callPhoneNumber,FloatingViewService.this));
        tags.add(getContactName(callPhoneNumber,FloatingViewService.this));
        final ImageView collapsedImageView = (ImageView) mFloatingView.findViewById(R.id.collapsed_iv);

        collapsedImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                Intent intent = new Intent("com.evernote.action.SEARCH");

                intent.putExtra(TAG_NAME_LIST,tags);

                Log.d("Everee","notebook ID is "+getnotenum);
                if(!getnotenum.equals(null))
                    intent.putExtra(NOTEBOOK_GUID,getnotenum);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }else
                    Toast.makeText(FloatingViewService.this, "Evernote Not Available", Toast.LENGTH_SHORT).show();



            }
        });

        //Set the close button
        ImageView closeButtonCollapsed = (ImageView) mFloatingView.findViewById(R.id.close_btn);
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.evernote.action.SEARCH_NOTES");

                startActivity(intent);
            }
        });




        //Open the application on thi button click


        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mSharedPreference = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(this);
        Boolean FloatAccept =mSharedPreference.getBoolean("floatButtonAccept",false);
        if (FloatAccept && (mFloatingView != null)){
            mWindowManager.removeView(mFloatingView);
            Log.d("Everee","OnDestroyAccepted");
        }

        else{
            Toast.makeText(this, "Switch on EverSimple to make notes", Toast.LENGTH_SHORT).show();
            Log.d("Everee","OnDestroyDeny");
        }
    }
    public String getContactName(final String phoneNumber, Context context)
    {
        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName="";
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }
        if(contactName.equals("")){
            contactName=phoneNumber;
        }
        return contactName;
    }


}
