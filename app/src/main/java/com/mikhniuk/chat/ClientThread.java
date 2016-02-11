package com.mikhniuk.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Observable;

/**
 * Created by Рома on 06.02.2016.
 */
public class ClientThread extends Observable implements Runnable  {
    private Socket socket;
    private BufferedReader reader;
    private boolean finished = false;
    private static final int SLEEP_TIME = 1000;
    private static final int SERVER_PORT = 4444;
    private String data;
    private static final String SERVER_IP = "46.101.96.234";
    private static OutputStream writer;

    @Override
    public void run() {
        try {
            this.socket = new Socket(SERVER_IP, SERVER_PORT);
            this.data = "";
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = socket.getOutputStream();
            String s;

            while (!data.equals("-")) {
                try {
                    Thread.sleep(SLEEP_TIME);
                    if (!finished) {
                        s = reader.readLine();
                        if (s == null) {
                            data = "no Internet";
                        }
                        data = s + "\n" + data;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            socket.close();
        } catch (IOException e) {
            data = "no Internet";
        } finally {
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

        /*public boolean getFinished(){
            return this.finished;
        }*/

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean MakeMail(String mail){
        try {
            if(this.writer == null){
                this.data += "null";
            }
            this.writer.write(mail.getBytes());
            this.writer.flush();
            return true;
        } catch (IOException e) {
            this.data += e.toString();
            return false;
        }catch(java.lang.NullPointerException e){
            this.data += e.toString();
            return false;
        }
    }

    public void Close() {
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getString() throws IOException {
        return data;
    }

        /*public String getString() throws IOException {
            if (reader.ready())
                return reader.readLine();
            return "No data";
        }

        public String getStringForce() throws IOException {
            return reader.readLine();
        }*/

}
