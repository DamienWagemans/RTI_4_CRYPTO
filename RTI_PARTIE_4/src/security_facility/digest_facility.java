package security_facility;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

public class digest_facility implements Serializable
{
    private String mdp;
    private String user;
    private long time;
    private double random;
    private byte[] message;

    public void Digest() throws IOException
    {
        try 
        {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            MessageDigest md = MessageDigest.getInstance("SHA-1", "BC");
            md.update(mdp.getBytes());
            md.update(user.getBytes());
                
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeLong(time);
            dos.writeDouble(random);
            
            md.update(baos.toByteArray());
            message = md.digest();
            mdp =null;
                    } 
        catch (NoSuchAlgorithmException | NoSuchProviderException ex) 
        {
            System.out.println("Erreur algorithm digest : " + ex);
        } 
    }

    public digest_facility() {
    }
    
    public digest_facility (String user, String mdp,long time,double random)
    {
        this.user = user;
        this.mdp = mdp;
        this.time = time;
        this.random = random;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setRandom(double random) {
        this.random = random;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }
    
    public digest_facility(digest_facility d, String mdp)
    {
        this.mdp = mdp;
        this.user = d.user;
        this.time = d.getTime();
        this.random = d.getRandom();
        this.message = d.getMessage();
    }
    
    public digest_facility(digest_facility d)
    {
        this.mdp = null;
        this.time = d.getTime();
        this.random = d.getRandom();
        this.user = d.user;
        this.message = d.getMessage();
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
