package com.mikhniuk.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;

/**
 * Created by Рома on 06.02.2016.
 */
public class ClientThread extends Observable implements Runnable {
    private Socket socket;
    private String ip = "46.101.96.234";
    private String user_id;
    private String exception;
    private int port = 4444;
    private BufferedReader reader;
    private boolean finish;
    private String message;
    private static ClientThread singleton;

    public static ClientThread getSingleton() {
        if (singleton == null) {
            singleton = new ClientThread();
            Thread thread = new Thread(singleton);
            thread.start();
        }
        return singleton;
    }

    private ClientThread() {
        finish = false;
    }

    /*
        public ClientThread() {
            finish = false;
        }
    */

    private String combine(String s){
        String ans = "";
        int i = s.length()-1;
        while(s.charAt(i) != ' '){
            ans = s.charAt(i) + ans;
            i--;
        }
        return ans;
    }

    public String getUser_id(){
        return user_id;
    }

    @Override
    public void run() {
        try {
            this.socket = new Socket(ip, port);
            while (!socket.isConnected()) {
                Thread.sleep(100);
            }
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            message = reader.readLine();
            user_id = combine(message);
            setChanged();
            notifyObservers();
            while (!finish) {
                message = reader.readLine();
                setChanged();
                notifyObservers();
                Thread.sleep(100);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();

        } finally {
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void finish() {
        finish = true;
        singleton = null;
    }

    public void sendMail(final String mail) {
        try {
            final PrintWriter writer = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())),
                    true);
            Thread tr = new Thread(new Runnable() {
                @Override
                public void run() {
                    writer.println(mail);
                }
            });
            tr.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMessage() {
        return message;
    }

    public String getException() {
        return exception;
    }
}
