/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProtocolCONTROLID;

import ClassesCONTROLID.Voiture;
import ClassesCONTROLID.Voyageur;
import interface_req_rep.Requete;
import database.*;
import divers.Config_Applic;
import static divers.Config_Applic.pathLogin;
import divers.Persistance_Properties;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.sql.*;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import requeteVERIFID.Reponse_VERIFID;
import requeteVERIFID.Requete_VERIFID;
import security_facility.asymetrique_facility;
import security_facility.digest_facility;
import security_facility.hmac_facility;
import security_facility.signature_facility;
import security_facility.symetrique_facility;
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
    public static int HANDSHAKE = 4;

    
    public static int STOP = -1;
    
    private int type;
    private Object classe = null;
    private Properties myProperties;
    
    private PublicKey PublicKey_Client_Frontiere = null;
    private PrivateKey PrivateKey_Serveur_National = null;
    private PublicKey PublicKey_Serveur_International = null;
    
    private SecretKey SecretKey_AUTH = null;
    private SecretKey SecretKey_CYPHER = null;
    
    
    public byte [] data = null;
    public byte [] data_2 = null;
    
    public RequeteCONTROLID() throws CertificateException 
    {
        myProperties = Persistance_Properties.LoadProp(pathLogin);
        setTypeRequete(0); 
        setObjectClasse(null); 
        chargement_cle();
    }
    public RequeteCONTROLID(int t, Object classe) throws CertificateException 
    {
        myProperties = Persistance_Properties.LoadProp(pathLogin);
        setTypeRequete(t); 
        setObjectClasse(classe); 
        chargement_cle();
    }
    
    public RequeteCONTROLID(int t, byte [] b) throws CertificateException 
    {
        myProperties = Persistance_Properties.LoadProp(pathLogin);
        setTypeRequete(t); 
        setObjectClasse(null);
        setData(b);
        chargement_cle();
        setData_2(null);
    }
    
    public RequeteCONTROLID(int t, Object classe, byte [] b) throws CertificateException 
    {
        myProperties = Persistance_Properties.LoadProp(pathLogin);
        setTypeRequete(t); 
        setObjectClasse(classe);
        data = b;
        data_2 = null;
        chargement_cle();
    }
    public RequeteCONTROLID(int t, Object classe, byte [] b, byte [] b2) throws CertificateException 
    {
        myProperties = Persistance_Properties.LoadProp(pathLogin);
        setTypeRequete(t); 
        setObjectClasse(classe);
        data = b;
        data_2 = b2;
        
        chargement_cle();
    }

    public byte[] getData_2() {
        return data_2;
    }

    public void setData_2(byte[] data_2) {
        this.data_2 = data_2;
    }
    
    
    public void chargement_cle() throws CertificateException 
    {
        //crypto
        //je dois recuperer les differentes cle dans les keystore
        Properties key = Persistance_Properties.LoadProp(Config_Applic.pathKEYstore_Serveur_National);
        PublicKey_Client_Frontiere = asymetrique_facility.get_cle_publique_asymetrique(key.getProperty("type_keystore"), key.getProperty("chemin_keystore"), key.getProperty("mdp_keystore"),key.getProperty("nom_certificat_client_frontiere"));
        PrivateKey_Serveur_National = asymetrique_facility.get_cle_privee_asymetrique(key.getProperty("type_keystore"),key.getProperty("chemin_keystore"), key.getProperty("mdp_keystore"), key.getProperty("name"), key.getProperty("mdp_keystore"));
        PublicKey_Serveur_International = asymetrique_facility.get_cle_publique_asymetrique(key.getProperty("type_keystore"), key.getProperty("chemin_keystore"), key.getProperty("mdp_keystore"),key.getProperty("nom_certificat_serveur_international"));
    
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
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
                try 
                {
                    TraitementRequete(s,Sock_internat, instruc);
                } 
                catch (CertificateException ex) 
                {
                    Logger.getLogger(RequeteCONTROLID.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
    }
    
    public void TraitementRequete(Socket s, Socket Sock_internat, Statement instruct) throws CertificateException 
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
                    case 4:
                        System.out.println("Dans HANDSHAKE");
                        handshake(s);
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
    
    
    public int TraiterRequeteLogin(Socket sock, Statement instruc) throws IOException, CertificateException
    {
        //ici je recupere l'objet digest qui l'username, le nombre aléatoire, et l'heure. Je vais recuperper
        // le mot de pass par rapoort a l'usernamet et je fais un hash de tout ca.
        // je compare ensuite les deux !
        RequeteCONTROLID req = new RequeteCONTROLID();
        digest_facility digest_recu = new digest_facility((digest_facility)getObjectClasse());
        
        
        String adresseDistante = sock.getRemoteSocketAddress().toString(); 
        System.out.println("Début de traiteRequete : adresse distante = " + adresseDistante);
        //je recupere le mot de pass correspont au username
        String pass = myProperties.getProperty(digest_recu.getUser());
        ReponseCONTROLID rep;
        if(pass != null){
            //si l'username existe
            //je recrée un nouveau digest pour pouvoir le recalculé et ensuite le comparer
            digest_facility digest_recalcule = new digest_facility(digest_recu, pass);
            
            System.out.println("calcul du digest");
            digest_recalcule.Digest();
            
            //comparaison des deux digest
            if(MessageDigest.isEqual(digest_recu.getMessage(), digest_recalcule.getMessage()))
            {
                rep = new ReponseCONTROLID(ReponseCONTROLID.LOGIN_OK, null);
                rep.EnvoieReponse(sock);
                return 1;
            }
            else
            {
                rep = new ReponseCONTROLID(ReponseCONTROLID.LOGIN_FAIL, null);
                rep.EnvoieReponse(sock);
                return 0;
            }
        }    
        else
        {
            rep = new ReponseCONTROLID(ReponseCONTROLID.LOGIN_FAIL, null);
            System.out.println(adresseDistante+" / User "+ digest_recu.getUser() + " : Bad Login  / " +Thread.currentThread().getName());
            rep.EnvoieReponse(sock);
            return 0;
        }

    }
    
    public void verif_voyageur(Socket sock, Socket sock_internat, Statement instruc) throws IOException, SQLException, ClassNotFoundException {
        Voyageur voyageur = new Voyageur();
        //ca c'est le message qui a été crypto par le client
        byte[] crypted_message = (this.getData());
        //ca c'est le message crypto qui a été hashé par le client
        byte[] crypted_message_authenticate_by_client = this.getData_2();
        //ce qu'on va faire c'est rehasher le message crypto de ce coté ci et voir si il est egal au messahe hashé par le client
        byte [] crypted_message_authenticate_by_server = hmac_facility.authentification(SecretKey_AUTH, crypted_message);
        if (MessageDigest.isEqual(crypted_message_authenticate_by_client, crypted_message_authenticate_by_server))
        {
            //mtn on va décrypted le message
            voyageur.byte_to_voyageur(symetrique_facility.decrypte_message(crypted_message, SecretKey_CYPHER));
            ReponseCONTROLID rep = new ReponseCONTROLID(ReponseCONTROLID.VOYAGEUR_FAIL, null);
            facility SqlInstruct  = new facility();
            //je verifie l'immatriculation dans BD_REGNAT, si elle n'exite pas ici, j'envoi la requete a serveur reg internat 
            ResultSet rs2 = SqlInstruct.SelectAllRowFromTable("PERSONNE", instruc);
            int find =0;
            while (rs2.next())
            {
                if(rs2.getString("N_REG_NAT").equals(voyageur.getN_REG()) )
                {
                    find = 1;
                    if(rs2.getString("PERMIS_DE_CONDUIRE").equals("O"))
                    {
                        System.out.println("Il a un permis");
                        voyageur.setPermis(true);
                    }
                    else {voyageur.setPermis(false);}
                    break;
                }
            }
            if(find == 1)
            {
                //la personne a été trouvée, pas besopin de revifier dans le serveur international
                crypted_message = symetrique_facility.encrypte_message(voyageur.voyageur_to_byte(), SecretKey_CYPHER);
                rep = new ReponseCONTROLID(ReponseCONTROLID.VOYAGEUR_OK, null, crypted_message, hmac_facility.authentification(SecretKey_AUTH, crypted_message));
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
                //je recupere le voyageur
                voyageur = new Voyageur((Voyageur)rep_internat.getClasse());
                //je crypte le voyageur
                crypted_message = symetrique_facility.encrypte_message(voyageur.voyageur_to_byte(), SecretKey_CYPHER);
                //j'envoi la requete contenant, le type, null car pas utilisé, le voyageur cyrpto, et un mash du voyageur crypté
                rep = new ReponseCONTROLID(rep_internat.getType(), null, crypted_message, hmac_facility.authentification(SecretKey_AUTH, crypted_message));
                rep.EnvoieReponse(sock);
            }
        }
        else
        {
            ReponseCONTROLID rep = new ReponseCONTROLID(ReponseCONTROLID.VOYAGEUR_AUTH_FAIL, null);
            rep.EnvoieReponse(sock);
            
        }
    }
    
    
    public void verif_immatriculation(Socket sock, Socket sock_internat, Statement instruc) throws SQLException, IOException, ClassNotFoundException
    {
        Voiture voiture = new Voiture((Voiture)this.getObjectClasse());
        //je verifie la signature en donnant en parametre le plaque d'immatriculation car c'est ce qui avait été envoyé a l'envoi,
        // clé publique nbecassaire pour le decryptage, this.getData c'est la signature qui a été générée par client frontiere

        if (signature_facility.VerifierSignature(PublicKey_Client_Frontiere, voiture.getImmatriculation(), this.getData()) == true)
        {
            ReponseCONTROLID rep = new ReponseCONTROLID(ReponseCONTROLID.IMMAT_FAIL, null);
            facility SqlInstruct  = new facility();
            //je verifie l'immatriculation dans BD_REGNAT, si elle n'exite pas ici, j'envoi la requete a serveur reg internat 
            ResultSet rs2 = SqlInstruct.SelectAllRowFromTable("VOITURE", instruc);
            int find =0;
            while (rs2.next())
            {
                if(rs2.getString("IMMATRICULATION").equals(voiture.getImmatriculation()) )
                {
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
                //je dois signer ma reponse pour la renvoyé au client frontiere
                System.out.println("Réponse envoyée au client");
                rep = new ReponseCONTROLID(ReponseCONTROLID.IMMAT_OK, voiture,signature_facility.signer(PrivateKey_Serveur_National, voiture.getImmatriculation()));
                rep.EnvoieReponse(sock);
            } 
            else
            {
                System.out.println("Pas trouvée, je demande au serveur international");
                //crypto : de nouveau, je crée une requete, contenant la voiture et la signature basée sur la plaque d'immatriculation
                // je signe avec ma clé privée, l'autre verifie ma signature avec ma clef publique
                Requete_VERIFID req_internat = new Requete_VERIFID(Requete_VERIFID.VERIF_IMMATRICULATION,voiture,signature_facility.signer(PrivateKey_Serveur_National, voiture.getImmatriculation()) );
                
                req_internat.EnvoieRequete(sock_internat);
                System.out.println("Envoi de la requete au serveur internat");
                Reponse_VERIFID rep_internat = new Reponse_VERIFID();
                rep_internat.RecevoirRequete(sock_internat);
                //je recupere la reponse du serveur internat et je la renvoi au client
                voiture = new Voiture((Voiture)rep_internat.getClasse());
                if (signature_facility.VerifierSignature(PublicKey_Serveur_International, voiture.getImmatriculation(), rep_internat.getData()))
                {
                    rep = new ReponseCONTROLID(rep_internat.getType(), voiture, signature_facility.signer(PrivateKey_Serveur_National, voiture.getImmatriculation()));
                    rep.EnvoieReponse(sock);
                }
                else
                {
                    //signature invalide..
                    Voiture v = new Voiture("SIGNATURE");
                    rep = new ReponseCONTROLID(ReponseCONTROLID.IMMAT_FAIL, voiture, signature_facility.signer(PrivateKey_Serveur_National, voiture.getImmatriculation()));
                    rep.EnvoieReponse(sock);
                } 
                

            }
        }
        else
        {
            System.out.println("Je passe directement dans l'invalidité !");
            Voiture v = new Voiture("SIGNATURE");
            ReponseCONTROLID rep = new ReponseCONTROLID(ReponseCONTROLID.IMMAT_FAIL, voiture, signature_facility.signer(PrivateKey_Serveur_National, voiture.getImmatriculation()));
            rep.EnvoieReponse(sock);
        }
    }
    
    
    public void handshake(Socket s) throws IOException, ClassNotFoundException, CertificateException
    {
        byte[] t = asymetrique_facility.decrypte_message_asymetrique(this.getData(), PrivateKey_Serveur_National);
        SecretKey_CYPHER = new SecretKeySpec(t, 0, t.length, "DES");      
        System.out.println("Premiere cle OK");

        this.RecevoirRequete(s);
        
        t = asymetrique_facility.decrypte_message_asymetrique(this.getData(), PrivateKey_Serveur_National);
        SecretKey_AUTH = new SecretKeySpec(t, 0, t.length, "DES");
        
        System.err.println("Clé auth " + SecretKey_AUTH);
        System.err.println("Clé Cypher " + SecretKey_CYPHER);
        
        ReponseCONTROLID rep = new ReponseCONTROLID();
        rep.setTypeRequete(ReponseCONTROLID.HANDSHAKE_OK);
        rep.EnvoieReponse(s);
        
    }
            
    
   
    public void EnvoieRequete(Socket cliSocket) throws IOException 
    { 
        ObjectOutputStream oos; 
        oos = new ObjectOutputStream(cliSocket.getOutputStream());
        oos.writeObject(this); 
        oos.flush();
    }
    
    public void RecevoirRequete(Socket CSocket) throws IOException, ClassNotFoundException, CertificateException
    { 
        ObjectInputStream ois=null; 
        RequeteCONTROLID temp = new RequeteCONTROLID();
        System.out.println("En attente d'une requete" + CSocket.toString());
        ois = new ObjectInputStream(CSocket.getInputStream());
        temp= (RequeteCONTROLID)ois.readObject();
        this.setTypeRequete(temp.getTypeRequete());
        this.setObjectClasse(temp.getObjectClasse());
        this.setData(temp.getData());
        this.setData_2(temp.getData_2());
        System.out.println("Requete lue par le serveur, instance de " + this.getClass().getName());
    }
    
    public int random(int high, int low) {
            return((int)(Math.random() * (high+1-low)) + low);
    }
        
}
