package security_facility;

import java.security.*;

public class signature_facility 
{
    public static byte[] signer(PrivateKey cle, String msg)
    {
        byte[] sb = null;
        try 
        {
            byte[] b = msg.getBytes();
            
            Signature s = Signature.getInstance("SHA1withRSA", "BC");
            s.initSign(cle);
            s.update(b);
            sb = s.sign();
        } 
        catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException ex) 
        {
            System.out.println("Signature : " + ex);
        } 
        
        return sb;
    }
    
    public static boolean VerifierSignature(PublicKey cle, String msg, byte[] signature)
    {
        boolean tmp = false;
        try 
        {
            byte[] b = msg.getBytes();
            
            Signature s = Signature.getInstance("SHA1withRSA", "BC");
            s.initVerify(cle);
            s.update(b);
            tmp = s.verify(signature);
        } 
        catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException ex) 
        {
            System.out.println("Signature : " + ex);
        } 
        
        return tmp;
    }   
}
