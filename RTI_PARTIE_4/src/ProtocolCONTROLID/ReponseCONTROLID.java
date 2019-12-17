/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProtocolCONTROLID;

import interface_req_rep.Reponse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 *
 * @author Dos Santos
 */
public class ReponseCONTROLID implements Reponse, Serializable{
    public static int LOGIN_OK = 201;
    public static int IMMAT_OK = 202;
    public static int VOYAGEUR_OK = 203;
    public static int HANDSHAKE_OK = 204;

    
    public static int LOGIN_FAIL = 501;
    public static int IMMAT_FAIL = 502;
    public static int VOYAGEUR_FAIL = 503;
    public static int VOYAGEUR_AUTH_FAIL = 504;
    
    private int type; 
    private Object classe;
    
    private byte [] data = null;
    private byte [] data_2 = null;
    
    public ReponseCONTROLID(int c, Object classe) {
        setTypeRequete(c); 
        setObjectClasse(classe); 
        data = null;
        data_2 = null;
    }
    
    public ReponseCONTROLID(int c, Object classe, byte[] b) {
        setTypeRequete(c); 
        setObjectClasse(classe);
        setData(b);
        setData_2(null);
    }
    public ReponseCONTROLID(int c, Object classe, byte[] b, byte [] b2) {
        setTypeRequete(c); 
        setObjectClasse(classe);
        setData(b);
        setData_2(b2);
    }
    
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    
    public ReponseCONTROLID() {
        setTypeRequete(0); 
        setObjectClasse(null); 
        data = null;
    }

    public byte[] getData_2() {
        return data_2;
    }

    public void setData_2(byte[] data_2) {
        this.data_2 = data_2;
    }
    
    public int getTypeRequete() { return type; }
    public void setTypeRequete(int type) {
        this.type = type; 
    }
    
    public Object getObjectClasse() { return classe; }
    public void setObjectClasse(Object classe) {
        this.classe = classe; 
    }

    @Override
    public int getCode() {
        return type;
    }
    
    public void RecevoirReponse(Socket cliSock) throws IOException, ClassNotFoundException{
        
        ObjectInputStream ois = null;
        ReponseCONTROLID temp = new ReponseCONTROLID();
        ois = new ObjectInputStream(cliSock.getInputStream());
        temp = (ReponseCONTROLID)ois.readObject();
        this.setObjectClasse(temp.getObjectClasse());
        this.setTypeRequete(temp.getTypeRequete());
        this.setData(temp.getData());
        this.setData_2(temp.getData_2());
        System.out.println(" *** Reponse recue : " + this.getTypeRequete());

    }
    
    public void EnvoieReponse(Socket CSocket) throws IOException{
        ObjectOutputStream oos; 
        oos = new ObjectOutputStream(CSocket.getOutputStream());
        oos.writeObject(this); 
        oos.flush(); 
    }
}
