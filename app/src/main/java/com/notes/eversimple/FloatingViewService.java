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
import android.os.Handler;
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
import android.widget.FrameLayout;
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
    private View mFloatingView;
    SharedPreferences mSharedPreference;
    private static final String TAG_NAME_LIST  = "TAG_NAME_LIST";
    private static final String NOTEBOOK_GUID="NOTEBOOK_GUID";
    public static final String QUERY="query";
    RelativeLayout mleft,mright;


    ImageView leftm,leftclose,rightm,rightclose;


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

        leftm =  mFloatingView.findViewById(R.id.collapsed_iv);

        leftclose=mFloatingView.findViewById(R.id.close_btn);
        rightclose=mFloatingView.findViewById(R.id.close_button);
       // GradientDrawable shapeDrawable = (GradientDrawable) leftm.getBackground();



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
//        switch (color){
//            case 1:
//                shapeDrawable.setColor(getResources().getColor(R.color.glasswhite));
//
//                break;
//            case 2:
//                shapeDrawable.setColor(getResources().getColor(R.color.evernoteoff));
//
//                break;
//            case 3:
//                shapeDrawable.setColor(getResources().getColor(R.color.glassBlack));
//
//                break;
//        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.START | Gravity.CENTER;
        params.x = -0;
        params.y = 0;
        //Initially view will be added to top-left corner





        final boolean FloatAccept =mSharedPreference.getBoolean("floatButtonAccept",false);
        final String getnotenum =mSharedPreference.getString("notebookGUID","none");
        //Add the view to the window
        if(FloatAccept){
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mFloatingView, params);
            Log.d("Everee","Open Float");
        }
        //The root element of the collapsed view layout
        final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        //The root element of the expanded view layout
        final View expandedView = mFloatingView.findViewById(R.id.expanded_container);


        Log.d("Everee","Phn Num Is : "+ callPhoneNumber);

        final ArrayList<String> tags=new ArrayList<String>();
        tags.add("Eversimple");
        Log.d("Everee","Name: "+getContactName(callPhoneNumber,FloatingViewService.this));
        tags.add(getContactName(callPhoneNumber,FloatingViewService.this));
        final String queryString=getContactName(callPhoneNumber,FloatingViewService.this);
        //final ImageView collapsedImageView = (ImageView) mFloatingView.findViewById(R.id.collapsed_iv);

        expandedView.setVisibility(View.VISIBLE);
        mFloatingView.findViewById(R.id.close_btn).setVisibility(View.GONE);
        mFloatingView.findViewById(R.id.collapsed_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expandedView.getVisibility()==View.GONE) {
                    expandedView.setVisibility(View.VISIBLE);
                    mFloatingView.findViewById(R.id.close_btn).setVisibility(View.GONE);
                }
                else {
                    expandedView.setVisibility(View.GONE);
                    mFloatingView.findViewById(R.id.close_btn).setVisibility(View.VISIBLE);
                }
            }
        });

        ImageView newnote = (ImageView) mFloatingView.findViewById(R.id.new_note);
        newnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        ImageView search=mFloatingView.findViewById(R.id.history);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.evernote.action.SEARCH_NOTES");

                intent.putExtra(QUERY,queryString);


                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }else
                    Toast.makeText(FloatingViewService.this, "Evernote Not Available", Toast.LENGTH_SHORT).show();
            }
        });

        leftclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DestroyRightView();

            }
        });
        rightclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFloatingView.findViewById(R.id.close_btn).setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
            }
        });

        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById(R.id.collapsed_iv).setOnTouchListener(new View.OnTouchListener() {
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
                        Log.d("Everee","Action Down");

                        break;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        Log.d("Everee","Action Move");

                        break;




                }
                return false;


            }
        });
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
                        Log.d("Everee","Action root Down");

                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        Log.d("Everee","Action root Move");
;

                }
                return true;
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
        if (FloatAccept ){

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
