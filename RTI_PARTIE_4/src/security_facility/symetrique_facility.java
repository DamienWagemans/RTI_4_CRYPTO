package security_facility;

import java.security.*;
import javax.crypto.*;

public class symetrique_facility 
{
    public static SecretKey get_cle_secrete_symetrique()
    {
        SecretKey cleSecret = null;
        try 
        {
            KeyGenerator cleGen = KeyGenerator.getInstance("DES", "BC");
            cleGen.init(new SecureRandom());
            
            cleSecret = cleGen.generateKey();
        } 
        catch (NoSuchAlgorithmException | NoSuchProviderException ex) 
        {
            System.out.println("Erreur CleSymetrique : " + ex);
        }
        
        return cleSecret;
    }
    
    public static byte[] encrypte_message(byte [] msgByte, SecretKey cle)
    {
        byte[] msgCrypte = null;
        try 
        {
            Cipher chiffrement = Cipher.getInstance("DES/ECB/PKCS5Padding", "BC");
            chiffrement.init(Cipher.ENCRYPT_MODE, cle);

            msgCrypte = chiffrement.doFinal(msgByte); 
        } 
        catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException ex) 
        {
            System.out.println("Erreur CleSymetrique : " + ex);
        } 
        
        return msgCrypte;
    }
    
    public static byte [] decrypte_message(byte[] b, SecretKey cle)
    {
        byte [] msgDecrypte = null;
        try 
        {
            Cipher chiffrement = Cipher.getInstance("DES/ECB/PKCS5Padding", "BC");
            chiffrement.init(Cipher.DECRYPT_MODE, cle);
            
            msgDecrypte = chiffrement.doFinal(b);
            
        } 
        catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException ex) 
        {
            System.out.println("Erreur CleSymetrique : " + ex);
        } 
        
        return msgDecrypte;
    }  
}
