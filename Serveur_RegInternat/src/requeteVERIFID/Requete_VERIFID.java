/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package requeteVERIFID;

import ClassesCONTROLID.Voiture;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 *
 * @author damien
 */
public class Requete_VERIFID implements Serializable{
    
    private int type;
    private Object classe = null;
    
    public static int VERIF_IMMATRICULATION = 1;
    public static int VERIF_VOYAGEUR = 2;

    public Requete_VERIFID () 
    {
        setType(0); 
        setClasse(null); 
    }
    public Requete_VERIFID (int t, Object classe) 
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

        Requete_VERIFID temp = new Requete_VERIFID();

        System.out.println("En attente d'une requete" + CSocket.toString());

        ois = new ObjectInputStream(CSocket.getInputStream());
        temp= (Requete_VERIFID)ois.readObject();
        this.setType(temp.getType());
        this.setClasse(temp.getClasse());
        System.out.println("Requete lue par le serveur, instance de " + this.getClass().getName());
    }
    
}
