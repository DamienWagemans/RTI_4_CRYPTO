/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClassesCONTROLID;

import java.io.Serializable;

/**
 *
 * @author Dos Santos
 */
public class Voiture implements Serializable{
    private String Immatriculation;
    //utiliser pour dire si en regle ou non
    private String Etat;

    public String getEtat() {
        return Etat;
    }

    public void setEtat(String Etat) {
        this.Etat = Etat;
    }

    public String getImmatriculation() {
        return Immatriculation;
    }

    public void setImmatriculation(String Immatriculation) {
        this.Immatriculation = Immatriculation;
    }

    public Voiture(String Immatriculation) {
        this.Immatriculation = Immatriculation;
        this.Etat = "inconnu";
    }

    public Voiture(String Immatriculation, String Etat) {
        this.Immatriculation = Immatriculation;
        this.Etat = Etat;
    }
    
    
    public Voiture (Voiture v)
    {
        this.Immatriculation = v.Immatriculation;
        this.Etat = v.Etat;
    }
    
}
