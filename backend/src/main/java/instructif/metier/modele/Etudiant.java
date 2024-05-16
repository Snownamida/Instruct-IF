/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package instructif.metier.modele;

import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author mbaratova
 */
@Entity
@DiscriminatorValue("Etudiant")
public class Etudiant extends Intervenant{
    private String universite;
    private String specialite;

    public Etudiant() {
    }

    public Etudiant(String login,String nom, String prenom, String telephone, int niveauMin, int niveauMax ,String universite, String specialite) {
        super(login,nom, prenom, telephone, niveauMin, niveauMax);
        this.universite = universite;
        this.specialite = specialite;
    }
    
    
    
    
    

    public String getUniversite() {
        return universite;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setUniversite(String universite) {
        this.universite = universite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }
    
    
    
}
