package com.socket;

import com.ui.ChatFrame;
import com.ui.ChatFrameNoSocket;
import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class SocketClient implements Runnable{
    public SocketClient client;
    public int port;
    public String serverAddr;
    public Socket socket;
    public ChatFrame ui;
    public String pat="";
    public ObjectInputStream In;
    public ObjectOutputStream Out;
    public SocketClient(ChatFrame frame) throws IOException{
        ui = frame; this.serverAddr = ui.serverAddr; this.port = ui.port;
        socket = new Socket(InetAddress.getByName(serverAddr), port);
        Out = new ObjectOutputStream(socket.getOutputStream());
        Out.flush();
        In = new ObjectInputStream(socket.getInputStream());
    }
    
    @Override
    public void run() {
        pat = ui.pathy;
        System.out.println("Pathsckcli1 : " + pat);
        boolean keepRunning = true;
        while(keepRunning){
            try {
                Message msg = (Message) In.readObject();
                if(msg.type.equals("message")){
                    if(msg.recipient.equals("All")){
                        ui.map.get("All").append(msg.sender+": "+msg.content+"\n");
                        System.out.println("ALL : "+msg.toString());
                    }else{
                        if(!ui.map.containsKey(msg.sender) && !msg.sender.equals(ui.username)){
                            ChatFrameNoSocket chatFrameNoSocket = new ChatFrameNoSocket(ui.username,msg.sender, this);
                            ui.map.put(msg.sender, chatFrameNoSocket.jTextArea1);
                            ui.map.get(msg.sender).append(msg.content+"\n");
                        }else{
                            if(msg.sender.equals(ui.username)){
                                ui.map.get(msg.recipient).append("Yo: "+msg.content+"\n");
                            }else{
                                ui.map.get(msg.sender).append( msg.content+"\n");
                            }
                        }
                    }
                }
                else if(msg.type.equals("login")){
                    if(msg.content.equals("TRUE")){                       
                        ui.jButton5.setEnabled(true);
                        ui.jTextArea1.append("[Boom.. > Me] : Login Successful\n");
                        ui.jTextField3.setVisible(false); 
                        ui.jLabel1.setVisible(false);
                          ui.jTextField1.setVisible(false);
                               ui.jLabel4.setVisible(false);
                                    ui.jButton1.setVisible(false);
                                    ui.jButton7.setVisible(false);
                    }
                    else{
                        ui.jTextArea1.append("[Boom.. > Me] : Login Failed\n");
                    }
                }
                else if(msg.type.equals("test")){
                    ui.jButton1.setEnabled(false);
                    ui.jTextField3.setEnabled(true); 
                    ui.jTextField1.setEditable(false);
                }
                else if(msg.type.equals("newuser")){
                    if(!msg.content.equals(ui.username)){
                        boolean exists = false;
                        for(int i = 0; i < ui.model.getSize(); i++){
                            if(ui.model.getElementAt(i).equals(msg.content)){
                                exists = true; break;
                            }
                        }
                        if(!exists){ ui.model.addElement(msg.content); }
                    }
                }
                else if(msg.type.equals("signup")){
                    if(msg.content.equals("TRUE")){
                        ui.jButton5.setEnabled(true);
                        ui.jTextArea1.append("[Boom.. > Me] : Singup Successful\n");
                    }
                    else{
                        ui.jTextArea1.append("[Boom.. > Me] : Signup Failed\n");
                    }
                }
                else if(msg.type.equals("signout")){
                    if(msg.content.equals(ui.username)){
                        ui.jTextArea1.append("["+ msg.sender +" > Me] : Bye\n");
                        ui.jTextField1.setEditable(true);
                        for(int i = 1; i < ui.model.size(); i++){
                            ui.model.removeElementAt(i);
                        }
                        ui.clientThread.stop();
                    }
                    else{
                        ui.model.removeElement(msg.content);
                        ui.jTextArea1.append("["+ msg.sender +" > All] : "+ msg.content +" has signed out\n");
                    }
                }
                else if(msg.type.equals("upload_req")){
                    if(JOptionPane.showConfirmDialog(ui, ("Accept '"+msg.content+"' from "+msg.sender+" ?")) == 0){
                        JFileChooser jf = new JFileChooser();
                        jf.setSelectedFile(new File(msg.content));
                        int returnVal = jf.showSaveDialog(ui);
                       
                        String saveTo = jf.getSelectedFile().getPath();
                        if(saveTo != null && returnVal == JFileChooser.APPROVE_OPTION){
                            Download dwn = new Download(saveTo, ui);
                            Thread t = new Thread(dwn);
                            t.start();
                            //send(new Message("upload_res", (""+InetAddress.getLocalHost().getHostAddress()), (""+dwn.port), msg.sender));
                            send(new Message("upload_res", ui.username, (""+dwn.port), msg.sender));
                        }
                        else{
                            send(new Message("upload_res", ui.username, "NO", msg.sender));
                        }
                    }
                    else{
                        send(new Message("upload_res", ui.username, "NO", msg.sender));
                    }
                }
                else if(msg.type.equals("upload_res")){
                    if(!msg.content.equals("NO")){
                        int kkk  = Integer.parseInt(msg.content);
                        String addr = msg.sender;
                        ui.jButton5.setEnabled(false); ui.jButton6.setEnabled(false);
                        System.out.println("Pathsckcli : " + pat);
                        Upload upl = new Upload(addr, kkk, pat, ui);
                        Thread t = new Thread(upl);
                        t.start();
                    }
                    else{
                        ui.jTextArea1.append("[Boom.. > Me] : "+msg.sender+" rejected file request\n");
                    }
                }
                else{
                    ui.jTextArea1.append("[Boom.. > Me] : Unknown message type\n");
                }
            }
            catch(HeadlessException ex) {
                keepRunning = false;
                ui.jTextArea1.append("[Boom.. > Me] : Connection Failure\n");
                ui.jButton1.setEnabled(true); ui.jTextField1.setEditable(true); 
               ui.jButton5.setEnabled(false); ui.jButton5.setEnabled(false);
                
                for(int i = 1; i < ui.model.size(); i++){
                    ui.model.removeElementAt(i);
                }
                
                ui.clientThread.stop();
                
                System.out.println("Exception SocketClient run()");
                ex.printStackTrace();
            } catch (IOException ex) {
                keepRunning = false;
                ui.jTextArea1.append("[Boom.. > Me] : Connection Failure\n");
                ui.jButton1.setEnabled(true); ui.jTextField1.setEditable(true); 
                ui.jButton5.setEnabled(false); ui.jButton5.setEnabled(false);
                
                for(int i = 1; i < ui.model.size(); i++){
                    ui.model.removeElementAt(i);
                }
                
                ui.clientThread.stop();
                
                System.out.println("Exception SocketClient run()");
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                keepRunning = false;
                ui.jTextArea1.append("[Boom.. > Me] : Connection Failure\n");
                ui.jButton1.setEnabled(true); ui.jTextField1.setEditable(true);
               ui.jButton5.setEnabled(false); ui.jButton5.setEnabled(false);
                
                for(int i = 1; i < ui.model.size(); i++){
                    ui.model.removeElementAt(i);
                }
                
                ui.clientThread.stop();
                
                System.out.println("Exception SocketClient run()");
                ex.printStackTrace();
            } catch (NumberFormatException ex) {
                keepRunning = false;
                ui.jTextArea1.append("[Boom.. > Me] : Connection Failure\n");
                ui.jButton1.setEnabled(true); ui.jTextField1.setEditable(true);
                ui.jButton5.setEnabled(false); ui.jButton5.setEnabled(false);
                
                for(int i = 1; i < ui.model.size(); i++){
                    ui.model.removeElementAt(i);
                }
                
                ui.clientThread.stop();
                
                System.out.println("Exception SocketClient run()");
                ex.printStackTrace();
            }
        }
    }
    
    public void send(Message msg){
        try {
            Out.writeObject(msg);
            Out.flush();
            System.out.println("Outgoing : "+msg.toString());
            if(msg.type.equals("message") && !msg.content.equals(".bye")){
                String msgTime = (new Date()).toString();
            }
        } 
        catch (IOException ex) {
            System.out.println("Exception SocketClient send()");
        }
    }
    
    public void closeThread(Thread t){
        t = null;
    }
}
