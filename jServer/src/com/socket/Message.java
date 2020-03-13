/*
CLASE PARA SUBIR ARCHIVOS
edit: gybranperez

*/
package com.socket;

import java.io.Serializable;

public class Message implements Serializable{
    
    private static final long serialVersionUID = 1L;
    public String type, sender, content, recipient;
    
    public Message(String type, String sender, String content, String recipient){
        this.type = type; this.sender = sender; this.content = content; this.recipient = recipient;
    }
    
    @Override
    public String toString(){
        return "{tipo='"+type+"', Emisor='"+sender+"', contenido='"+content+"', Receptor='"+recipient+"'}";
    }

    public int getChId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setChId(long chId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setTimestamp(long l) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
