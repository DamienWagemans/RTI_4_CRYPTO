/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClassesCONTROLID;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * @author damien
 */
public class Voyageur implements Serializable{
    private String N_REG;
    private boolean Permis = false;

    public Voyageur(String N_REG, boolean permis) {
        this.N_REG = N_REG;
        this.Permis = permis;
    }
    
    public Voyageur()
    {
        this.N_REG = null;
        this.Permis = false; 
    }

    public Voyageur(Voyageur v)
    {
        this.N_REG = v.getN_REG();
        this.Permis = v.isPermis();
    }
    
    
    public String getN_REG() {
        return N_REG;
    }

    public void setN_REG(String N_REG) {
        this.N_REG = N_REG;
    }

    public boolean isPermis() {
        return Permis;
    }

    public void setPermis(boolean Permis) {
        this.Permis = Permis;
    }
    
    
        
    public byte [] voyageur_to_byte() throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        oos.flush();
        byte [] data = bos.toByteArray();
        return data;
    }
    
    public void byte_to_voyageur (byte [] b) throws IOException, ClassNotFoundException
    {
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b));
        Voyageur v = (Voyageur) in.readObject();
        this.N_REG = v.getN_REG();
        this.Permis = v.isPermis();
        in.close();
    }
    
}
