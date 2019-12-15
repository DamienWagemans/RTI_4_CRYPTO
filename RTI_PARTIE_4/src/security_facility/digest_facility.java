package security_facility;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class digest_facility implements Serializable
{
    private String mdp;
    private String user;
    private long time;
    private double random;
    private byte[] message;

    public void Digest()
    {
        try 
        {
            MessageDigest md = MessageDigest.getInstance("SHA-1", "BC");
            md.update(mdp.getBytes());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeLong(time);
            dos.writeDouble(random);
            
            md.update(baos.toByteArray());
            message = md.digest();
            mdp =null;
                    } 
        catch (NoSuchAlgorithmException | NoSuchProviderException | IOException ex) 
        {
            System.out.println("Erreur algorithm digest : " + ex);
        } 
    }
    
    public digest_facility (String user, String mdp,long time,double random)
    {
        this.user = user;
        this.mdp = mdp;
        this.time = time;
        this.random = random;
    }
    
    public digest_facility(digest_facility d, String mdp)
    {
        this.mdp = mdp;
        this.time = d.getTime();
        this.random = d.getRandom();
    }

    public String getMdp() {
        return mdp;
    }

    public String getUser() {
        return user;
    }

    public long getTime() {
        return time;
    }

    public double getRandom() {
        return random;
    }

    public byte[] getMessage() {
        return message;
    }
    
}
