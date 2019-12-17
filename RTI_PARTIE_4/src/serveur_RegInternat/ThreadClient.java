/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveur_RegInternat;

import ClassesCONTROLID.Voiture;
import ClassesCONTROLID.Voyageur;
import divers.Config_Applic;
import divers.Persistance_Properties;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import requeteVERIFID.Reponse_VERIFID;
import requeteVERIFID.Requete_VERIFID;
import security_facility.asymetrique_facility;
import security_facility.signature_facility;

/**
 *
 * @author damien
 */
public class ThreadClient extends Thread{ 
    private Socket Sock;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Statement instruc;
    private Requete_VERIFID req;
    private Reponse_VERIFID rep;
    
    private PrivateKey PrivateKey_Serveur_International = null;
    private PublicKey PublicKey_Serveur_National = null;
    
    
    public ThreadClient(Socket s, Statement st) throws CertificateException 
    {
        Sock = s;
        instruc = st;
        req = new Requete_VERIFID();
        rep = new Reponse_VERIFID();
        Properties key = Persistance_Properties.LoadProp(Config_Applic.pathKEYstore_Serveur_International);
        PublicKey_Serveur_National = asymetrique_facility.get_cle_publique_asymetrique(key.getProperty("type_keystore"), key.getProperty("chemin_keystore"), key.getProperty("mdp_keystore"),key.getProperty("nom_certificat_serveur_national"));
        PrivateKey_Serveur_International = asymetrique_facility.get_cle_privee_asymetrique(key.getProperty("type_keystore"),key.getProperty("chemin_keystore"), key.getProperty("mdp_keystore"), key.getProperty("name"), key.getProperty("mdp_keystore"));
        
    }
    public void run() 
    {
        int ret;
        try 
        {
            while(!isInterrupted())
            {
                System.out.println("Serveur RegInternat: thread crée : en attente d'une requete");
                //System.out.println("socket !!! : "+ Sock.toString());
                req.RecevoirRequete(Sock);
                System.out.println("Type de la requete  :" + req.getType());
                switch(req.getType())
                {
                    //verification d'immatriculation
                    case 1:
                        //crypto SIGNATURE !! 
                        Voiture v = new Voiture((Voiture)req.getClasse());
        
                        if (signature_facility.VerifierSignature(PublicKey_Serveur_National, v.getImmatriculation(), req.getData()))
                        {
                            System.out.println("Je suis dans serveur internat immatriculation recuperée : "+ v.getImmatriculation());
                            int find =0;
                            ResultSet rs2 = database.facility.SelectAllRowFromTable("VOITURE", instruc);
                            while (rs2.next())
                            {
                                if(rs2.getString("IMMATRICULATION").equals(v.getImmatriculation()) )
                                {
                                    System.out.println("Immatriculation trouvée !");
                                    find = 1;
                                    if(rs2.getString("VOLEE").equals("O") || rs2.getString("ASSURANCE").equals("N")|| rs2.getString("CONTROLE_TECHNIQUE").equals("N"))
                                    {
                                        System.out.println("Pas en règle");
                                        rep.setType(Reponse_VERIFID.IMMAT_FAIL);
                                        //voiture pas en ordre, controle necessaire..
                                        v.setEtat("CONTROLE");
                                    }
                                    else
                                    {
                                        System.out.println("En règle");
                                        rep.setType(Reponse_VERIFID.IMMAT_OK);
                                        v.setEtat("OK");
                                    }
                                    break;
                                }
                            }
                            if(find == 1)
                            {
                                //l'immatriculation a été trouvée, pas besopin de revifier dans le serveur international
                                System.out.println("Réponse envoyée au client");
                                rep.setClasse(v);
                                //je set la signature
                                rep.setData(signature_facility.signer(PrivateKey_Serveur_International, v.getImmatriculation()));
                                rep.EnvoieRequete(Sock);
                            } 
                            else
                            {
                                rep.setType(Reponse_VERIFID.IMMAT_FAIL);
                                v.setEtat("INCONNU");
                                rep.setClasse(v);
                                //je set la signature
                                rep.setData(signature_facility.signer(PrivateKey_Serveur_International, v.getImmatriculation()));
                                rep.setClasse(v);
                                System.out.println("Je passe bien dans inconnu !!!" + v.getEtat());
                                
                                rep.EnvoieRequete(Sock);
                            }
                        }
                        else
                        {
                            //signature invalide !! mais je dois signer mon message....penible...
                            Voiture voiture = new Voiture("SIGNATURE");
                            Reponse_VERIFID rep = new Reponse_VERIFID(Reponse_VERIFID.IMMAT_FAIL, voiture,signature_facility.signer(PrivateKey_Serveur_International, voiture.getImmatriculation()) );
                            rep.EnvoieRequete(Sock);
                        }    
                        break;
                    case 2:
                        Voyageur voyageur = (Voyageur)req.getClasse();
                        System.out.println("Je suis dans serveur internat personne recuperée : "+ voyageur.getN_REG());
                        int find_1 = 0;
                        ResultSet rs = database.facility.SelectAllRowFromTable("PERSONNE", instruc);
                        while (rs.next())
                        {
                            if(rs.getString("N_REG_INTERNAT").equals(voyageur.getN_REG()) )
                            {
                                System.out.println("Personne trouvée !");
                                find_1 = 1;
                                if(rs.getString("PERMIS_DE_CONDUIRE").equals("O") )
                                {
                                    voyageur.setPermis(true);
                                }
                                else
                                {
                                    voyageur.setPermis(false);
                                }
                                break;
                            }
                        }
                        if(find_1 == 1)
                        {
                            //la personne a été trouvée, pas besopin de revifier dans le serveur international
                            System.out.println("Réponse envoyée au client");
                            rep.setType(Reponse_VERIFID.VOYAGEUR_OK);
                            rep.setClasse(voyageur);
                            rep.EnvoieRequete(Sock);
                        } 
                        else
                        {
                            rep.setType(Reponse_VERIFID.VOYAGEUR_FAIL);
                            voyageur.setN_REG("INCONNU");
                            rep.setClasse(voyageur);
                            rep.EnvoieRequete(Sock);
                        }
                        break;
                }
                
                
                
            }
            System.out.println("je sors");
        } catch (IOException | ClassNotFoundException  | SQLException  ex) {
            Logger.getLogger(ThreadClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
