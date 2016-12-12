package com.hecz.androidgsr;

import android.os.Handler;
import android.os.Message;

/**
 * Created by Marek on 12.3.2016.
 */

public class PlayFile implements Runnable {
    public static final int MSG_PLAY_FILE = 100;
    private final int SEND_DATA_INTERVAL = 500;
    private Handler handler;
    private int[] arrayValuesNumber;
    private int[] arrayTimesNumber;
    private volatile boolean threadIsRunning = false;
    private volatile boolean runThread = true;
    private volatile boolean pauseThread = false;
    private int interval = SEND_DATA_INTERVAL;
    private int numberOfValues;

    public PlayFile() {
    }

    public void start(Handler handler, int[] arrayValuesNumber, int[] arrayTimesNumber, int numberOfValues) {
        this.handler = handler;
        this.arrayValuesNumber = arrayValuesNumber;
        this.arrayTimesNumber = arrayTimesNumber;
        this.numberOfValues = numberOfValues;

        //* check if thread is running
        if (threadIsRunning == true) {
            //* stop thread
            runThread = false;
            //* wait for thread stop
            while (threadIsRunning) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //* enable start thread
            runThread = true;
        }
        //* start new thread
        Thread thread = new Thread(this);
        thread.start();
    }

    public void pause(boolean pause) {
        pauseThread = pause;
    }

    public void sendFast(boolean fast) {
        if (fast) {
            this.interval = SEND_DATA_INTERVAL / 2;
        } else {
            this.interval = SEND_DATA_INTERVAL;
        }
    }

    @Override
    public void run() {
        int counter = 0;
        threadIsRunning = true;
        int runFirstTime = 1;

        while (runThread) {
            Message msg = handler.obtainMessage(MSG_PLAY_FILE);
            //* set resistance value
            msg.arg1 = arrayValuesNumber[counter];
            //* set flag whether plot should by reset
            msg.arg2 = runFirstTime;
            //* send message
            handler.sendMessage(msg);
            //* reset flag
            runFirstTime = 0;

            //* finish thread when all of data was played
            if (numberOfValues == ++counter) {
                break;
            }

            try {
                //* sleep, ktory casuje posielanie dat do grafu
                Thread.sleep(interval);
                //* slucka, ktora pauzne posielanie dat do grafu
                while(pauseThread && runThread) {
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
        //* indicator of finished thread
        threadIsRunning = false;
    }
}
