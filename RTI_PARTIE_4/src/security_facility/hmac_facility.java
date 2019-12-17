package security_facility;

import javax.crypto.*;
import java.security.*;

public class hmac_facility 
{
    public static byte[] authentification(SecretKey cle, byte [] data) 
    {
        byte[] hb = null;
        try 
        {
            Mac h = Mac.getInstance("HMAC-MD5", "BC");
            h.init(cle);
            h.update(data);
            hb = h.doFinal();
        } 
        catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException ex) 
        {
            System.out.println("HMAC : " + ex);
        }
        
        return hb;
    } 
}
