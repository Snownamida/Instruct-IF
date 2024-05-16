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
@DiscriminatorValue("Enseignant")
public class Enseignant extends Intervenant {
    private String typeEtablissementExercice;

    public Enseignant() {
    }

    public Enseignant(String login,String nom, String prenom, String telephone, int niveauMin, int niveauMax, String typeEtablissementExercice) {
        super(login,nom, prenom, telephone, niveauMin, niveauMax);
        this.typeEtablissementExercice = typeEtablissementExercice;
    }

    
    
    public String getTypeEtablissementExercice() {
        return typeEtablissementExercice;
    }

    public void setTypeEtablissementExercice(String typeEtablissementExercice) {
        this.typeEtablissementExercice = typeEtablissementExercice;
    }
    
    
}
