package com.mikhniuk.chat;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    public TextView text;
    private ClientThread clientThread;
    private Thread thread = null;
    public TextView internet;
    private boolean finished = false;
    private static final int SLEEP_TIME = 1000;
    private boolean hedlerstart = false;
    private EditText mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        text = (TextView) findViewById(R.id.textView);
        internet = (TextView) findViewById(R.id.status);
        mail = (EditText) findViewById(R.id.mail);
        internet.setTextColor(Color.GREEN);
        if (isOnline(getApplicationContext())) {
            clientThread = new ClientThread();
            thread = new Thread(clientThread);
            thread.start();
            Start(null);
        } else {
            internet.setTextColor(Color.RED);
            internet.setText("NOT");
        }
    }

    public void Update(View v) {
        try {
            text.setText(clientThread.getString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SendMail(View v) {
        if (isOnline(getApplicationContext())) {
            if (clientThread == null) {
                clientThread = new ClientThread();
                thread = new Thread(clientThread);
                thread.start();
                Start(null);
                clientThread.MakeMail(mail.getText().toString());
                Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_LONG).show();
            } else {
                clientThread.MakeMail(mail.getText().toString());
                Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_LONG).show();
            }
        } else {
            internet.setTextColor(Color.RED);
            internet.setText("NOT");
        }
        ;

    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private Runnable r1 = new Runnable() {
        @Override
        public void run() {
            try {
                String data = "";
                //Thread.sleep(SLEEP_TIME);
                SystemClock.sleep(SLEEP_TIME);
                data = clientThread.getString();
                if (data == "no Internet") {
                    internet.setTextColor(Color.RED);
                    internet.setText("NOT");
                    data = "";
                } else if (data != "" && internet.getText().toString().equals("NOT")) {
                    internet.setTextColor(Color.GREEN);
                    internet.setText("OK");
                }
                text.setText(data);
                if (!finished) {
                    Handler h = new Handler();
                    h.postDelayed(r1, 0);
                } else {
                    hedlerstart = false;
                    //clientThread.setFinished(true);
                }
                /*else{
                    text.setText(text.getText() + data);
                    if(!clientThread.finished){
                        Handler h = new Handler();
                        h.postDelayed(r2, 0);
                    }
                }*/

            } catch (IOException e) {
                text.setText(e.toString());
            }

        }
    };

    public void Close(View v) {
        clientThread.Close();
        finish();
    }

    public void Start(View v) {
        finished = false;
        if (isOnline(getApplicationContext())) {
            if (thread == null) {
                clientThread = new ClientThread();
                thread = new Thread(clientThread);
                thread.start();
            }
            if (!hedlerstart) {
                hedlerstart = true;
                Handler h = new Handler();
                h.postDelayed(r1, 1);
            }

        } else {
            internet.setTextColor(Color.RED);
            internet.setText("NOT");
            Toast toast = Toast.makeText(getApplicationContext(), "Нет интернета", Toast
                    .LENGTH_LONG);
            toast.show();
        }
    }
}


