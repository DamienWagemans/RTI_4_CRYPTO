package security_facility;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Vector;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class asymetrique_facility 
{
 public static PrivateKey get_cle_privee_asymetrique(String type, String path, String pwd, String name, String pwd_name) throws CertificateException
    {
        PrivateKey clePrivate = null;
        try 
        {
            KeyStore ks = KeyStore.getInstance(type);
            ks.load(new FileInputStream(path), pwd.toCharArray());
            
            clePrivate = (PrivateKey)ks.getKey(name, pwd_name.toCharArray());
        } 
        catch (NoSuchAlgorithmException | KeyStoreException | IOException | CertificateException | UnrecoverableKeyException ex) 
        {
            System.out.println("Erreur ClePriveeAsymetrique : " + ex);
        }
        return clePrivate;
    }
    
    public static PublicKey get_cle_publique_asymetrique(String type, String path, String pwd, String name_certif)
    {
        PublicKey clePublic = null;
        try 
        {
            KeyStore ks = KeyStore.getInstance(type);

            ks.load(new FileInputStream(path), pwd.toCharArray());

            X509Certificate certif = (X509Certificate)ks.getCertificate(name_certif);
            //System.err.println(certif);
            clePublic = certif.getPublicKey();
            
        } 
        catch (NoSuchAlgorithmException | KeyStoreException | IOException | CertificateException ex) 
        {
            System.out.println("Erreur ClePubliqueAsymetrique : " + ex);
        } 
        return clePublic;
    }
    
    public static byte[] encrypte_message_asymetrique(byte[] msgByte, PublicKey cle)
    {
        byte[] msgCrypte = null;
        try 
        {
            Cipher chiffrement = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
            chiffrement.init(Cipher.ENCRYPT_MODE, cle);
            
            msgCrypte = chiffrement.doFinal(msgByte); 
        } 
        catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException ex) 
        {
            System.out.println("Erreur CleAsymetrique : " + ex);
        } 
        
        return msgCrypte;
    }
    
    public static byte[] decrypte_message_asymetrique(byte[] msg, PrivateKey cle)
    {
        byte[] msgDecrypte = null;
        try 
        {
            Cipher chiffrement = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
            chiffrement.init(Cipher.DECRYPT_MODE, cle);
            
            msgDecrypte = chiffrement.doFinal(msg);
        } 
        catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException ex) 
        {
            System.out.println("Erreur CleAsymetrique : " + ex);
        } 
        
        return msgDecrypte;
    } 

    public static void affichage_keystore(String type,String path, String pwd)
    {
        try 
        {
            KeyStore ks = KeyStore.getInstance(type);
            ks.load(new FileInputStream(path), pwd.toCharArray());
            Enumeration en = ks.aliases();
            String alias_courrant = null;
            Vector vector_aliases = new Vector();
            while(en.hasMoreElements())vector_aliases.add(en.nextElement());
            Object[] aliases = vector_aliases.toArray();System.err.println("taille aliases.lenght : " + aliases.length);
            for(int i = 0; i < aliases.length; i++)
            {
                if(ks.isKeyEntry(alias_courrant=(String)aliases[i]))
                    System.err.println((i+1) + ".[KEY ENTRY] " + aliases[i].toString());
                else if(ks.isCertificateEntry(alias_courrant))
                    System.err.println((i+1) + ".[TRUSTED CERTIFICATE ENTRY] " + aliases[i].toString());                 
            }      
        } 
        catch (NoSuchAlgorithmException | KeyStoreException | IOException | CertificateException ex) 
        {
            System.out.println("Erreur ClePubliqueAsymetrique : " + ex);
        }        
    }
}
