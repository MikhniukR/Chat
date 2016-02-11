package com.mikhniuk.chat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class LoginActivity extends Activity implements Observer {

    private ClientThread client;
    private LinearLayout linerl;
    private LinearLayout.LayoutParams params;
    private Handler handler;
    private EditText mymail;
    private ArrayList<TextView> mails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        client = new ClientThread();
        client.addObserver(this);
        TextView internet = (TextView) findViewById(R.id.internet);
        mails = new ArrayList<TextView>();
        mymail = (EditText) findViewById(R.id.mail);
        if (isOnline(getApplicationContext())) {
            Thread thread = new Thread(client);
            linerl = (LinearLayout) findViewById(R.id.liner);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                    .LayoutParams.WRAP_CONTENT);
            thread.start();
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (!client.getMessage().equals("")) {
                        mails.add(new TextView(getApplicationContext()));
                        mails.get(mails.size() - 1).setText(client.getMessage());
                        mails.get(mails.size() - 1).setTextColor(Color.parseColor("#16BC35"));
                        linerl.addView(mails.get(mails.size() - 1), 0, params);
                    } else {

                    }
                }
            };
        } else {
            internet.setTextColor(Color.RED);
            internet.setText("NOT");
        }
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

    public void Close(View v) {
        client.finish();
    }

    public void SendMail(View v) {
        client.sendMail(mymail.getText().toString());
        mymail.setText("");
    }

    @Override
    public void update(Observable observable, Object data) {
        handler.sendEmptyMessage(1);
    }
}