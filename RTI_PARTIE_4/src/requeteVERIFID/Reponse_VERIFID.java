/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package requeteVERIFID;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 *
 * @author damien
 */
public class Reponse_VERIFID implements Serializable{
    
    private int type;
    private Object classe = null;
    
    public static int LOGIN_OK = 201;
    public static int IMMAT_OK = 202;
    public static int VOYAGEUR_OK = 203;
    
    public static int LOGIN_FAIL = 501;
    public static int IMMAT_FAIL = 502;
    public static int VOYAGEUR_FAIL = 503;
    

    public Reponse_VERIFID () 
    {
        setType(0); 
        setClasse(null); 
    }
    public Reponse_VERIFID (int t, Object classe) 
    {
        setType(t); 
        setClasse(classe); 
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getClasse() {
        return classe;
    }

    public void setClasse(Object classe) {
        this.classe = classe;
    }
    
    public void EnvoieRequete(Socket cliSocket) throws IOException 
    { 
        ObjectOutputStream oos; 
        oos = new ObjectOutputStream(cliSocket.getOutputStream());
        oos.writeObject(this); 
        oos.flush();

    }
    
    public void RecevoirRequete(Socket CSocket) throws IOException, ClassNotFoundException
    { 
        ObjectInputStream ois=null; 
        Reponse_VERIFID temp = new Reponse_VERIFID();
        System.out.println("En attente d'une requete" + CSocket.toString());
        ois = new ObjectInputStream(CSocket.getInputStream());
        temp= (Reponse_VERIFID)ois.readObject();
        this.setType(temp.getType());
        this.setClasse(temp.getClasse());
        System.out.println("Requete lue par le serveur, instance de " + this.getClass().getName());
    }

}
