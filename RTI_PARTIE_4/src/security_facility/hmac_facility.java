package security_facility;

import javax.crypto.*;
import java.security.*;

public class hmac_facility 
{
    public static byte[] authentification(SecretKey cle, String msg)
    {
        byte[] hb = null;
        try 
        {
            byte[] b = msg.getBytes();
            
            Mac h = Mac.getInstance("HMAC-MD5", "BC");
            h.init(cle);
            h.update(b);
            hb = h.doFinal();
        } 
        catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException ex) 
        {
            System.out.println("HMAC : " + ex);
        }
        
        return hb;
    } 
}
