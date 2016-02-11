package com.mikhniuk.chat;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Observable;

/**
 * Created by Рома on 06.02.2016.
 */
public class ClientThread extends Observable implements Runnable {
    private Socket socket;
    private String ip;
    private String exception;
    private int port;
    private BufferedReader reader;
    private static OutputStream writer;
    private boolean finish;
    private String message;

    ClientThread(String ip, int port) {
        this.ip = ip;
        this.port = port;
        finish = false;
    }

    @Override
    public void run() {
        try {
            this.socket = new Socket(ip, port);
            this.writer = socket.getOutputStream();
            while (!socket.isConnected()) {
                Thread.sleep(100);
            }
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (!finish) {
                message = reader.readLine();
                setChanged();
                notifyObservers();
                Thread.sleep(100);
            }
        } catch (IOException e) {
            message = "";
            exception ="No Internet";
            setChanged();
            notifyObservers();
        } catch (InterruptedException e) {
            message = "";
            exception ="Exception";
            setChanged();
            notifyObservers();
        } finally {
            try {
                if(socket!=null)
                    socket.close();
            } catch (IOException e) {
            }
        }
    }

    public void finish() {
        finish = true;
    }

    public void sendMail(String mail, Context cont){
        mail = mail + "+";
        try {
            this.writer.write(mail.getBytes());
            this.writer.flush();
            Toast.makeText(cont, "OK", Toast.LENGTH_LONG).show();
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
