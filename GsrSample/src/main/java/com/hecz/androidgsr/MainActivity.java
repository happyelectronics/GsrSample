package com.hecz.androidgsr;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hecz.android.FlexPlayer;
import com.hecz.android.IFlexListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.INVISIBLE;
import static android.view.View.OnClickListener;
import static android.view.View.OnTouchListener;
import static android.view.View.VISIBLE;
//komentar mainus

public class MainActivity extends AppCompatActivity implements IFlexListener {

    Toast toast = null;
    Media media = new Media();
    FlexPlayer flexPlayer = new FlexPlayer();
    PlayFile playFile = new PlayFile();
    StringBuilder stringBuilder = new StringBuilder();
    PointF firstFinger;

    float distBetweenFingers;

    boolean stopThread = false;
    public boolean isStarted = false;
    boolean recordClicked = false;

    private static int SAMPLE_SIZE = 1000;
    private static final int NONE = 0;
    private static final int ONE_FINGER_DRAG = 1;
    private static final int TWO_FINGERS_DRAG = 2;

    public static final String APP = "GSR";
    public static final String GSR_HISTORY_DIR_NAME = "GSR History";
    public static final String SOUND_FILE_NAME = "now.3gp";

    String historyDirectory;

    int mess;
    int mode = NONE;
    int helper;
    int counter = 0;

    int[] ArrayValuesNumber = new int[SAMPLE_SIZE];
    int[] arrayTimesNumber = new int[SAMPLE_SIZE];

    @BindView(R.id.textView5)
    TextView buyFlex;
    @BindView(R.id.textView3)
    TextView textStatus;
    @BindView(R.id.textView)
    TextView gsr;

    @BindView(R.id.buttonStop)
    Button stopGsr;
    @BindView(R.id.buttonRecord)
    Button btnRecordWithSound;

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            mess = msg.what;
            switch (msg.what) {
                case FlexPlayer.MSG_CONNECT:
                    isStarted = true;

                    Log.i(APP, "handler, connect***");
                    textStatus.setText("Online");
                    textStatus.setTextColor(getResources().getColor(R.color.greenStart));
                    showButtons();
                    Toast.makeText(getApplicationContext(), "FlexPlayer connected",
                            Toast.LENGTH_SHORT).show();

                    break;


                case FlexPlayer.MSG_DISCONNECT:
                    isStarted = false;
                    Log.d(APP, "odpojeni " + "spadlo to tady ");
                    //hideButtons();

                    textStatus.setText("Offline");
                    textStatus.setTextColor(getResources().getColor(R.color.warning));


                    Toast.makeText(getApplicationContext(), "FlexPlayer disconnected",
                            Toast.LENGTH_SHORT).show();
                    break;


                case FlexPlayer.MSG_GSRRECEIVED:

                    Log.i(APP, "GSR RECEIVED " + "gsr = " + flexPlayer.getGsr());
                    gsr.setText("Actual Resistance: " + flexPlayer.getGsr() + " kΩ");

                    long unixTime = System.currentTimeMillis() / 1000L;
                    stringBuilder.append(flexPlayer.getGsr() + ";" + counter + "\n");
                    counter++;
                    //finalString = stringBuilder.toString(); //Add new value twice in second

                    break;

                //* play offline data from files
                case PlayFile.MSG_PLAY_FILE:

                    //* get actual data though msg
                    int actualResistance = msg.arg1;
                    boolean clearPlot = (msg.arg2 == 1 ? true : false);
                    gsr.setText("Actual Resistance " + actualResistance + " kΩ");

                    //* pokial je naposledy zobrazeny cas rozdielny od aktualneho rozsahu, tak zobrazime aktualny

                    break;

                //* this case is called when audio file is loaded into buffer
                case Media.MSG_AUDIO_PREPARED:
                    gsr.setVisibility(View.VISIBLE);
                    //* start to draw data from file
                    playFile.start(handler, ArrayValuesNumber, arrayTimesNumber, helper);
                    //* play sound file
                    media.playStart();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenLock.lockOrientation(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(R.drawable.back24);
        setSupportActionBar(toolbar);

        //* tento toast sa pouziva na zobrazenie zvolenenho casoveho rozsahu grafu
        toast = Toast.makeText(getApplicationContext()
                , ""
                , Toast.LENGTH_SHORT);


        gsr.setVisibility(INVISIBLE);

        // FlexPlayer Initialization
        flexPlayer.initPort(this, this);
        media.setHandler(handler);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);

        if (!flexPlayer.isConnected()) {
            textStatus.setText("Offline");
            textStatus.setTextColor(getResources().getColor(R.color.warning));
            stopGsr.getBackground().setAlpha(255);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onDestroy() {
        flexPlayer.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        buyFlex = (TextView) findViewById(R.id.textView5);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (prefs.getBoolean("klic", true) == false)
            buyFlex.setVisibility(INVISIBLE);
        if (prefs.getBoolean("klic", true) == true)
            buyFlex.setVisibility(VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent showPrefs = new Intent(MainActivity.this, PrefsActivity.class);
                startActivity(showPrefs);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnected() {
        Message msg = handler.obtainMessage();
        msg = handler.obtainMessage(FlexPlayer.MSG_CONNECT);
        handler.sendMessage(msg);
    }

    @Override
    public void onDisconnect() {
        Message msg = handler.obtainMessage();
        msg = handler.obtainMessage(FlexPlayer.MSG_DISCONNECT);
        handler.sendMessage(msg);
    }

    @OnClick(R.id.buttonRecord)
    public void record() {
        if (!recordClicked) {
            if (textStatus.getText().toString().equals("Offline")) {
                return;
            }

            btnRecordWithSound.getBackground().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
            stopGsr.getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);

            gsr.setVisibility(VISIBLE);
            flexPlayer.startGsr(handler);
            Toast.makeText(getApplicationContext(), "Measuring will start in few seconds. \n Keep steady until the probe warm up.", Toast.LENGTH_SHORT).show();
            recordClicked = true;
        } else {
            stop();
            recordClicked = false;
        }
    }

    @OnClick(R.id.buttonStop)
    public void stop() {
        btnRecordWithSound.getBackground().setColorFilter(getResources().getColor(R.color.warning), PorterDuff.Mode.SRC_ATOP);
        stopGsr.getBackground().setColorFilter(getResources().getColor(R.color.gray_bg), PorterDuff.Mode.SRC_ATOP);
        recordClicked = false;
        flexPlayer.stopGsr();
        gsr.setVisibility(INVISIBLE);
    }

    public void showButtons() {
        stopGsr.getBackground().setAlpha(255);
        stopGsr.setClickable(true);
    }

    public void onTextClick(View v) {
        String url = "https://www.happy-electronics.eu/shop/en/home/10-emotional-sensor-skin-response.html"; //
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
