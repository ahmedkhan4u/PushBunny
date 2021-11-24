package com.pushbunny;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.onesignal.OSDeviceState;
import com.onesignal.OSMutableNotification;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal;
import com.pushbunny.Adapters.NotificationAdapter;
import com.pushbunny.Database.DatabaseHelper;
import com.pushbunny.Models.NotificationModel;

import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String ONESIGNAL_APP_ID = "7dcb66d6-ad57-4d98-8dc1-d0649b3307ec";
    // 7dcb66d6-ad57-4d98-8dc1-d0649b3307ec One Signal App Id
    // NzY4ZWFjOTQtYjc5Mi00NjZmLWIwYmItZjExNTcwNDk1N2Vm Api Key
    public static DatabaseHelper db;
    public static RecyclerView recyclerView;
    public static List<NotificationModel> list;
    public static NotificationAdapter adapter;
    private LinearLayout layout;
    private boolean status = false;
    CountDownTimer cTimer = null;
    private EditText searchView;
    private ImageView imgMenu;
    public static Context context;
    private String userId;
    private CountDownTimer countDownTimer;
    private LinearLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        context = MainActivity.this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        layout = findViewById(R.id.layout);
        list = new ArrayList();
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        imgMenu = findViewById(R.id.menu);
        progressBar = findViewById(R.id.progressBar);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        startTimer();

        setupUI(findViewById(R.id.layout));

        OneSignal.setNotificationWillShowInForegroundHandler(new OneSignal.OSNotificationWillShowInForegroundHandler() {
            @Override
            public void notificationWillShowInForeground(OSNotificationReceivedEvent notificationReceivedEvent) {
                OSNotification notification = notificationReceivedEvent.getNotification();

                notificationReceivedEvent.complete(notification);

                if (notification != null || !notification.equals("{}") || !notification.equals("")){
                    Log.d("dxdiag" , "Receiveddd..");

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            // Stuff that updates the UI
                            getDataFromDb(db, list);
                            adapter.notifyDataSetChanged();
                            Log.d("dxdiag" , "Ui Thread..");

                        }
                    });
                }

            }
        });

        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog dialog = new BottomSheetDialog(MainActivity.this);
                dialog.setContentView(R.layout.settings_view);
                TextView txtUserId = dialog.findViewById(R.id.userid);
                txtUserId.setText(userId);
                AppCompatButton btnDeleteAll = dialog.findViewById(R.id.btnDeleteAll);

                btnDeleteAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        deleteAllMessages();
                    }
                });

                txtUserId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", userId);
                            clipboard.setPrimaryClip(clip);
                        Toast.makeText(MainActivity.this, "Copied", Toast.LENGTH_SHORT).show();

                    }
                });
                dialog.show();
            }
        });

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(MainActivity.this, imgMenu);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        deleteAllMessages();
                        return true;
                    }
                });

                popup.show(); //showing popup menu

            }
        });

        db = new DatabaseHelper(this);
        Log.d("dxdiag : ", "Hello Push Bunny");

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        OneSignal.setNotificationOpenedHandler(new OneSignal.OSNotificationOpenedHandler() {
            @Override
            public void notificationOpened(OSNotificationOpenedResult result) {
                Log.d("dxdiag : ", "Result : " + result);
                getDataFromDb(db, list);
                adapter = new NotificationAdapter(MainActivity.this, list);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });


        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        getDataFromDb(db, list);
//        adapter = new NotificationAdapter(MainActivity.this, list);
//        recyclerView.setAdapter(adapter);

    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                userId = OneSignal.getDeviceState().getUserId();
                if (userId == null || userId.isEmpty()){
                    startTimer();
                }else {
                    countDownTimer.cancel();
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

            }
        }.start();
    }


    void showToast() {
        Toast.makeText(MainActivity.this, "Hello", Toast.LENGTH_SHORT).show();
    }

    private void deleteAllMessages() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setTitle("Are you sure to delete all messages?");
        builder1.setMessage("By selecting \"OK\" your all messages will be deleted");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        db.deleteAll();
                        getDataFromDb(db, list);
                        list.clear();
                        adapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        alert11.show();
    }

    public static void getDataFromDb(DatabaseHelper db, List<NotificationModel> list) {

        list.clear();

        try {

            Cursor res = db.getAllData();
            if (res.getCount() == 0) {
                Log.d("dxdiag", "Something went wrong no data in db");
                return;
            }
            while (res.moveToNext()){
                list.add(new NotificationModel(res.getString(0),res.getString(1),
                        res.getString(2),res.getString(3), res.getString(4), res.getString(5)));
            }

            Collections.reverse(list);

        } catch (Exception ex){
            Log.d("dxdiag", "Get DB Data Exception : " + ex);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromDb(db, list);
        adapter = new NotificationAdapter(MainActivity.this, list);
        recyclerView.setAdapter(adapter);
        adapter.filter(searchView.getText().toString().trim());
    }

    @Override
    public void onBackPressed() {
        if (!searchView.getText().toString().trim().isEmpty()){
            searchView.setText("");
            return;
        }
        super.onBackPressed();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(MainActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}