/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClassesCONTROLID;

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
    
    
}
