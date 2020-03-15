package com.socket;

import com.ui.ChatFrame;
import java.io.*;
import java.net.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Upload implements Runnable{

    public String addr;
    public int port;
    public Socket socket;
    public FileInputStream In;
    public OutputStream Out;
    public File file;
    public String pat,patch;
    public ChatFrame ui;
    
    public Upload(String addr, int port, String filepath, ChatFrame frame){
        super();
        try {
            FileReader archivo = new FileReader("path.txt");
            BufferedReader buffi = new BufferedReader(archivo);
            patch = buffi.readLine();
            buffi.close();
            socket = new Socket(InetAddress.getByName(addr), port);
            System.out.println("Ejecuto socket");
            Out = socket.getOutputStream();
            System.out.println("Ejecuto out");
            In = new FileInputStream(patch);
            System.out.println("Ejecuto in");
            this.pat = patch; 
            System.out.println("Ejecuto pat");
            System.out.println("Ejecuto iiu");
            ui = frame;
        } 
        catch (Exception ex) {
            System.out.println("Exception [Upload : Upload(...)]");
        }
        System.out.println("Path1 : " + pat);
        
    }
    
    @Override
    public void run() {
        try {       
            byte[] buffer = new byte[1024];
            int count;
            System.out.println("Path: " + pat);
            while((count = In.read(buffer)) >= 0){
                Out.write(buffer, 0, count);
            }
            Out.flush();
            
            ui.jTextArea1.append("[Boom > Me] : File upload complete\n");
            ui.jButton5.setEnabled(true); ui.jButton6.setEnabled(true);
           
            
            if(In != null){ In.close(); }
            if(Out != null){ Out.close(); }
            if(socket != null){ socket.close(); }
        }
        catch (Exception ex) {
            System.out.println("Exception [Upload : run()]");
            ex.printStackTrace();
        }
    }

}