/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProtocolCONTROLID;

import ClassesCONTROLID.Voiture;
import ClassesCONTROLID.Login;
import ClassesCONTROLID.Voyageur;
import interface_req_rep.Requete;
import database.*;
import static divers.Config_Applic.pathLogin;
import divers.Persistance_Properties;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import requeteVERIFID.Reponse_VERIFID;
import requeteVERIFID.Requete_VERIFID;
/**
 *
 * @author Dos Santos
 */
//SERVEUR RECOIT UNE REQUETE ET ENVOIE UNE REPONSE
//CLIENT ENVOIE UNE REQUETE ET RECOIT UNE REPONSE
public class RequeteCONTROLID implements Requete , Serializable{
    
    public static int LOGIN = 1;
    public static int VERIF_IMMATRICULATION = 2;
    public static int VERIF_VOYAGEUR = 3;

    
    public static int STOP = -1;
    
    private int type;
    private Object classe = null;
    private Properties myProperties;
    
     public RequeteCONTROLID() 
    {
        myProperties = Persistance_Properties.LoadProp(pathLogin);
        setTypeRequete(0); 
        setObjectClasse(null); 
    }
    public RequeteCONTROLID(int t, Object classe) 
    {
        myProperties = Persistance_Properties.LoadProp(pathLogin);
        setTypeRequete(t); 
        setObjectClasse(classe); 
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
    public Runnable createRunnable(Socket s, Socket Sock_internat, Statement instruc) {
        return new Runnable() 
        {
            public void run() {
                TraitementRequete(s,Sock_internat, instruc);
            }
        };
    }
    
    public void TraitementRequete(Socket s, Socket Sock_internat, Statement instruct) 
    {
        int Login = 0;
        try 
        {
            
            while(this.getTypeRequete() != -1)
            {
                while (Login == 0)
                {
                    Login = TraiterRequeteLogin(s, instruct);
                    this.RecevoirRequete(s);
                }
                
                switch(this.getTypeRequete())
                {
                    case 2:
                        System.out.println("Dans verif immat");
                        verif_immatriculation(s, Sock_internat,instruct);
                        
                        break;
                    case 3 : 
                        System.out.println("Dans verif voyageur");
                        verif_voyageur(s, Sock_internat, instruct);
                        break;

                      
                }
                this.RecevoirRequete(s);

            } 
        } catch (IOException ex ) {
            Logger.getLogger(RequeteCONTROLID.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RequeteCONTROLID.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(RequeteCONTROLID.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    
    public int TraiterRequeteLogin(Socket sock, Statement instruc) throws IOException
    {
        
        RequeteCONTROLID req = new RequeteCONTROLID();
        Login log = new Login((Login)getObjectClasse());
        String adresseDistante = sock.getRemoteSocketAddress().toString(); 
        System.out.println("Début de traiteRequete : adresse distante = " + adresseDistante);
        String pass = myProperties.getProperty(log.getUsername());
        
        ReponseCONTROLID rep;
        if(pass != null){
            rep = new ReponseCONTROLID(ReponseCONTROLID.LOGIN_OK, null);
            System.out.println(adresseDistante+" / User "+ log.getUsername() + " : Login OK / "  +Thread.currentThread().getName());
            rep.EnvoieReponse(sock);
            return 1;
        }    
        else
        {
            rep = new ReponseCONTROLID(ReponseCONTROLID.LOGIN_FAIL, null);
            System.out.println(adresseDistante+" / User "+ log.getUsername() + " : Bad Login  / " +Thread.currentThread().getName());
            rep.EnvoieReponse(sock);
            return -1;
        }

    }
    
    public void verif_voyageur(Socket sock, Socket sock_internat, Statement instruc) throws IOException, SQLException, ClassNotFoundException {
        String adresseDistante = sock.getRemoteSocketAddress().toString(); 
        Voyageur voyageur = new Voyageur((Voyageur)this.getObjectClasse());
        ReponseCONTROLID rep = new ReponseCONTROLID(ReponseCONTROLID.VOYAGEUR_FAIL, null);
        facility SqlInstruct  = new facility();
        //je verifie l'immatriculation dans BD_REGNAT, si elle n'exite pas ici, j'envoi la requete a serveur reg internat 
        ResultSet rs2 = SqlInstruct.SelectAllRowFromTable("PERSONNE", instruc);
        System.out.println("Voici le numero de registre a trouver  a trouver : " + voyageur.getN_REG());
        int find =0;
        while (rs2.next())
        {
            System.out.println("Ce que j'ai : " + voyageur.getN_REG()+ "//" + voyageur.getN_REG().length());
            System.out.println("Dans la db : " + rs2.getString("N_REG_NAT") + "//" + rs2.getString("N_REG_NAT"));
            if(rs2.getString("N_REG_NAT").equals(voyageur.getN_REG()) )
            {
                System.out.println("Personne trouvée !");
                find = 1;
                if(rs2.getString("PERMIS_DE_CONDUIRE").equals("O"))
                {
                    System.out.println("Il a un permis");
                    voyageur.setPermis(true);
                }
                else
                {
                    voyageur.setPermis(false);
                }
                break;
            }
        }
        if(find == 1)
        {
            //la personne a été trouvée, pas besopin de revifier dans le serveur international
            System.out.println("Réponse envoyée au client");
            rep.setObjectClasse(voyageur);
            rep.EnvoieReponse(sock);
        } 
        else
        {
            System.out.println("Pas trouvée, je demande au serveur international");
            Requete_VERIFID req_internat = new Requete_VERIFID(Requete_VERIFID.VERIF_VOYAGEUR,voyageur);
            req_internat.EnvoieRequete(sock_internat);
            System.out.println("Envoi de la requete au serveur internat");
            Reponse_VERIFID rep_internat = new Reponse_VERIFID();
            rep_internat.RecevoirRequete(sock_internat);
            //je recupere la reponse du serveur internat et je la renvoi au client
            rep.setObjectClasse(rep_internat.getClasse());
            rep.setTypeRequete(rep_internat.getType());
            rep.EnvoieReponse(sock);

        }
        
    }
    
    
    public void verif_immatriculation(Socket sock, Socket sock_internat, Statement instruc) throws SQLException, IOException, ClassNotFoundException
    {
        Voiture voiture = new Voiture((Voiture)this.getObjectClasse());
        ReponseCONTROLID rep = new ReponseCONTROLID(ReponseCONTROLID.IMMAT_FAIL, null);
        facility SqlInstruct  = new facility();
        //je verifie l'immatriculation dans BD_REGNAT, si elle n'exite pas ici, j'envoi la requete a serveur reg internat 
        ResultSet rs2 = SqlInstruct.SelectAllRowFromTable("VOITURE", instruc);
        System.out.println("Voici l'immatriculation a trouver : " + voiture.getImmatriculation());
        int find =0;
        while (rs2.next())
        {
            System.out.println("dans la db : " + rs2.getString("IMMATRICULATION"));
            if(rs2.getString("IMMATRICULATION").equals(voiture.getImmatriculation()) )
            {
                System.out.println("Immatriculation trouvée !");
                find = 1;
                if(rs2.getString("VOLEE").equals("O") || rs2.getString("ASSURANCE").equals("N")|| rs2.getString("CONTROLE_TECHNIQUE").equals("N"))
                {
                    System.out.println("Pas en règle");
                    rep.setTypeRequete(ReponseCONTROLID.IMMAT_FAIL);
                    //voiture pas en ordre, controle necessaire..
                    voiture.setEtat("CONTROLE");
                }
                else
                {
                    System.out.println("En règle");
                    rep.setTypeRequete(ReponseCONTROLID.IMMAT_OK);
                    voiture.setEtat("OK");
                }
                break;
            }
        }
        if(find == 1)
        {
            //l'immatriculation a été trouvée, pas besopin de revifier dans le serveur international
            System.out.println("Réponse envoyée au client");
            rep.setObjectClasse(voiture);
            rep.EnvoieReponse(sock);
        } 
        else
        {
            System.out.println("Pas trouvée, je demande au serveur international");
            Requete_VERIFID req_internat = new Requete_VERIFID(Requete_VERIFID.VERIF_IMMATRICULATION,voiture);
            req_internat.EnvoieRequete(sock_internat);
            System.out.println("Envoi de la requete au serveur internat");
            Reponse_VERIFID rep_internat = new Reponse_VERIFID();
            rep_internat.RecevoirRequete(sock_internat);
            //je recupere la reponse du serveur internat et je la renvoi au client
            rep.setObjectClasse(rep_internat.getClasse());
            rep.setTypeRequete(rep_internat.getType());
            rep.EnvoieReponse(sock);

        }
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
        RequeteCONTROLID temp = new RequeteCONTROLID();
        System.out.println("En attente d'une requete" + CSocket.toString());
        ois = new ObjectInputStream(CSocket.getInputStream());
        temp= (RequeteCONTROLID)ois.readObject();
        this.setTypeRequete(temp.getTypeRequete());
        this.setObjectClasse(temp.getObjectClasse());
        System.out.println("Requete lue par le serveur, instance de " + this.getClass().getName());
    }
    
    public int random(int high, int low) {
            return((int)(Math.random() * (high+1-low)) + low);
    }
        
}
