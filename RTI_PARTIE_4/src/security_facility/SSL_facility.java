/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security_facility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 *
 * @author damien
 */
public class SSL_facility 
{
    public static SSLServerSocket create_SSL_Server_socket (String KStype, String KSpath, String KSpassword, String KEYpassword, int port) throws KeyStoreException, FileNotFoundException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException
    {
        SSLServerSocket SslSSocket = null;

        //chargement du keystore
        KeyStore ServerKs = KeyStore.getInstance("JKS");
        String FICHIER_KEYSTORE = KSpath;
        char[] PASSWD_KEYSTORE = KSpassword.toCharArray(); 
        FileInputStream ServerFK = new FileInputStream (FICHIER_KEYSTORE); 
        ServerKs.load(ServerFK, PASSWD_KEYSTORE);
        
        //contexte
        SSLContext SslC = SSLContext.getInstance("SSLv3"); 
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509"); 
        char[] PASSWD_KEY = KEYpassword.toCharArray();
        kmf.init(ServerKs, PASSWD_KEY);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509"); 
        tmf.init(ServerKs);
        SslC.init(kmf.getKeyManagers(), 
        tmf.getTrustManagers(), null);
        
        //factory
        SSLServerSocketFactory SslSFac= SslC.getServerSocketFactory();
        
        //creation de la socket
        SslSSocket = (SSLServerSocket) SslSFac.createServerSocket(port);
        
        return SslSSocket; 
    }
    
    public static SSLSocket create_SSL_client_socket (String KStype, String KSpath, String KSpassword, String KEYpassword, int port, String ip) throws KeyStoreException, FileNotFoundException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException
    {
        SSLSocket SslSocket = null;

        //chargement du keystore
        KeyStore ServerKs = KeyStore.getInstance("JKS");
        String FICHIER_KEYSTORE = KSpath;
        char[] PASSWD_KEYSTORE = KSpassword.toCharArray(); 
        FileInputStream ServerFK = new FileInputStream (FICHIER_KEYSTORE); 
        ServerKs.load(ServerFK, PASSWD_KEYSTORE);
        
        //contexte
        SSLContext SslC = SSLContext.getInstance("SSLv3"); 
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509"); 
        char[] PASSWD_KEY = KEYpassword.toCharArray();
        kmf.init(ServerKs, PASSWD_KEY);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509"); 
        tmf.init(ServerKs);
        SslC.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        
        //factory
        SSLSocketFactory SslSFac= SslC.getSocketFactory();
        
        //creation de la socket
        SslSocket = (SSLSocket) SslSFac.createSocket(ip,port);
        System.err.println("Socket crées ! ");
        
        return SslSocket; 
    }
}
