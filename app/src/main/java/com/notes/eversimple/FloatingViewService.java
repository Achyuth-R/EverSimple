package com.notes.eversimple;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import ng.max.slideview.SlideView;

import static android.content.Intent.getIntent;
import static android.content.Intent.getIntentOld;


public class FloatingViewService extends Service{
    public static String callPhoneNumber;

    private WindowManager mWindowManager;
    private View mFloatingView,mFloatingViewRight;
    SharedPreferences mSharedPreference;
    private static final String TAG_NAME_LIST  = "TAG_NAME_LIST";
    private static final String NOTEBOOK_GUID="NOTEBOOK_GUID";
    public static final String QUERY="query";
    RelativeLayout mleft,mright;


    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreference = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(this);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startMyOwnForeground();
        }
        else{
            startForeground(1, new Notification());
        }


        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        mFloatingViewRight = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget_right, null);
        mleft=mFloatingViewRight.findViewById(R.id.collapse_viewr);
        mright=mFloatingView.findViewById(R.id.collapse_view);
        GradientDrawable shapeDrawable = (GradientDrawable) mright.getBackground();
        GradientDrawable shapeDrawabler = (GradientDrawable) mleft.getBackground();


        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        int y=100;

        int position = mSharedPreference.getInt("pmPos",2);
        int color=mSharedPreference.getInt("pmColor",2);

        Log.d("Everee","Post "+ position);
        switch (color){
            case 1:
                shapeDrawable.setColor(getResources().getColor(R.color.glasswhite));
                shapeDrawabler.setColor(getResources().getColor(R.color.glasswhite));
                break;
            case 2:
                shapeDrawable.setColor(getResources().getColor(R.color.evernoteoff));
                shapeDrawabler.setColor(getResources().getColor(R.color.evernoteoff));
                break;
            case 3:
                shapeDrawable.setColor(getResources().getColor(R.color.glassBlack));
                shapeDrawabler.setColor(getResources().getColor(R.color.glassBlack));
                break;
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        //Specify the view position
        switch (position){
            case 1:
                params.gravity = Gravity.START | Gravity.TOP;
                break;
            case 2:
                params.gravity = Gravity.START | Gravity.CENTER;
                break;
            case 3:
                params.gravity = Gravity.START | Gravity.BOTTOM;
                break;
        }

        //Initially view will be added to top-left corner
        params.x = -0;
        params.y = 0;

        final WindowManager.LayoutParams paramsRight = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        //Specify the view position
        switch (position){
            case 1:
                paramsRight.gravity = Gravity.END | Gravity.TOP;
                break;
            case 2:
                paramsRight.gravity = Gravity.END | Gravity.CENTER;
                break;
            case 3:
                paramsRight.gravity = Gravity.END | Gravity.BOTTOM;
                break;
        }
           //Initially view will be added to top-left corner
        paramsRight.x=0;
        paramsRight.y = 0;


        final Boolean FloatAccept =mSharedPreference.getBoolean("floatButtonAccept",false);
        final String getnotenum =mSharedPreference.getString("notebookGUID","none");
        //Add the view to the window
        if(FloatAccept){
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mFloatingView, params);
            mWindowManager.addView(mFloatingViewRight, paramsRight);
            Log.d("Everee","Open Float");
        }

        Log.d("Everee","Phn Num Is : "+ callPhoneNumber);

        final ArrayList<String> tags=new ArrayList<String>();
        tags.add("Eversimple");
        Log.d("Everee","Name: "+getContactName(callPhoneNumber,FloatingViewService.this));
        tags.add(getContactName(callPhoneNumber,FloatingViewService.this));
        final String queryString=getContactName(callPhoneNumber,FloatingViewService.this);
        //final ImageView collapsedImageView = (ImageView) mFloatingView.findViewById(R.id.collapsed_iv);
        SlideView slideView = (SlideView) mFloatingView.findViewById(R.id.slideView);
        SlideView slideViewRight = (SlideView) mFloatingViewRight.findViewById(R.id.slideViewRight);

        slideViewRight.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {

                Intent intent = new Intent("com.evernote.action.CREATE_NEW_NOTE");

                intent.putExtra(TAG_NAME_LIST,tags);

                Log.d("Everee","notebook ID is "+getnotenum);

                intent.putExtra(NOTEBOOK_GUID,getnotenum);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }else
                    Toast.makeText(FloatingViewService.this, "Evernote Not Available", Toast.LENGTH_SHORT).show();


            }
        });

        slideView.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {

                Intent intent = new Intent("com.evernote.action.SEARCH_NOTES");

                intent.putExtra(QUERY,queryString);
                DestroyRightView();

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }else
                    Toast.makeText(FloatingViewService.this, "Evernote Not Available", Toast.LENGTH_SHORT).show();
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
        boolean FloatAccept =mSharedPreference.getBoolean("floatButtonAccept",false);
        if (FloatAccept && (mFloatingViewRight != null)){
            mWindowManager.removeView(mFloatingViewRight);
           try {
               if(mFloatingView!=null)
                   DestroyRightView();
           }catch (Exception e){
               Log.d("Everee",e.getMessage());
           }
            Log.d("Everee","OnDestroyAccepted");
        }


        else{
            Toast.makeText(this, "Switch on EverSimple to make notes", Toast.LENGTH_SHORT).show();
            Log.d("Everee","OnDestroyDeny");
        }
    }

    public void DestroyRightView(){
        if (mFloatingView != null){
            mWindowManager.removeView(mFloatingView);
            Log.d("Everee","OnDestroyAcceptedRight");
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
