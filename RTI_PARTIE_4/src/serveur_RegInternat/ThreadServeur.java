/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveur_RegInternat;

import Serveur_National.ListeTaches;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import database.*;
import divers.Config_Applic;
import divers.Persistance_Properties;
import java.io.FileNotFoundException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
/**
 *
 * @author damien
 */
public class ThreadServeur extends Thread{
    private int port;
    private int nbr_client;
    private ServerSocket SSocket = null;
    private SSLServerSocket sslSsock = null;
    private Statement instruc;
    public int SSL;

    public Statement getInstruc() {
        return instruc;
    }

    public void setInstruc(Statement instruc) {
        this.instruc = instruc;
    }
    
    
    public ServerSocket getSSocket() {
        return SSocket;
    }

    public void setSSocket(ServerSocket SSocket) {
        this.SSocket = SSocket;
    }

    
    
    public ThreadServeur(int p, int nbr_c, int ssl) 
    {
        nbr_client = nbr_c;
        port = p; 
        SSL = ssl;
    }
    
    public void run() 
    {
        try 
        {
            if(SSL == 1)
            {
                System.err.println("Mode SSL");
                Properties key = Persistance_Properties.LoadProp(Config_Applic.pathKEYstore_Serveur_International);
                sslSsock = security_facility.SSL_facility.create_SSL_Server_socket(key.getProperty("type_keystore"), key.getProperty("chemin_keystore"), key.getProperty("mdp_keystore"), key.getProperty("mdp_keystore"), port);
                System.out.println("Socket : "+ sslSsock.toString());
            }
            else
            { 
                System.err.println("Mode non secure");
                SSocket = new ServerSocket(port);
            }
        }
        catch (IOException e) 
        {
            System.err.println("Erreur de port d'écoute ! ? [" + e + "]"); 
            System.exit(1); 
        } catch (KeyStoreException ex) {
            Logger.getLogger(ThreadServeur.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ThreadServeur.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(ThreadServeur.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnrecoverableKeyException ex) {
            Logger.getLogger(ThreadServeur.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(ThreadServeur.class.getName()).log(Level.SEVERE, null, ex);
        }
        try
        {
            //Connexion à la base de donnée
            System.out.println("Essai de connexion JDBC");
            Class leDriver= Class.forName("oracle.jdbc.driver.OracleDriver");
            
            //connexion a la nouvelle bd
            
            Connection con= DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/ORCL","BD_INTERNAT","Damien");
            //Connection con= DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE","RTI2","unizuniz1999");
            System.out.println("Connexion à la BDD RTI réalisée");
            instruc= con.createStatement();
            System.out.println("Création d'une instance d'instruction pour cette connexion");

            Socket CSocket = null;
            SSLSocket SSLSock = null;
            while (!isInterrupted()) 
            {
                
                System.out.println("************ Serveur en attente");
                if(SSL == 1)
                {
                    SSLSock = (SSLSocket)sslSsock.accept();
                    ThreadClient thr = new ThreadClient(SSLSock, instruc);
                    thr.start();
                }
                else
                {
                    CSocket = SSocket.accept();
                    ThreadClient thr = new ThreadClient(CSocket, instruc);
                    thr.start();
                }
                                 
                System.out.println("Etablissement d'une connexion");
                
                
                

            }
        }catch (SQLException ex) {
            Logger.getLogger(ThreadServeur.class.getName()).log(Level.SEVERE, null, ex);
        }catch (ClassNotFoundException ex) {
            Logger.getLogger(ThreadServeur.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException e) 
        {
            System.err.println("Erreur d'accept ! ? [" + e.getMessage() + "]"); 
            System.exit(1); 
        } catch (CertificateException ex) {
            Logger.getLogger(ThreadServeur.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    } 
}
