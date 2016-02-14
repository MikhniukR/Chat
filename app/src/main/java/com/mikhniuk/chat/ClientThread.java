package com.mikhniuk.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
    private String exception;
    private int port = 4444;
    private BufferedReader reader;
    private static OutputStream writer;
    private boolean finish;
    private String message;
    private static ClientThread singleton;

    public static ClientThread getSingleton(){
        if(singleton == null){
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

    public void sendMail(String mail){
        try {
            final PrintWriter writer = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())),
                    true);
            final String finalMail = mail;
            Thread tr = new Thread(new Runnable() {
                @Override
                public void run() {
                    writer.println(finalMail);
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
