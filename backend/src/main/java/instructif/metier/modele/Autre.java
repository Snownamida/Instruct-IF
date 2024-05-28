/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package instructif.metier.modele;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author mbaratova
 */
@Entity
@DiscriminatorValue("Autre")
public class Autre extends Intervenant {
    private String activite;

    public Autre() {
    }

    public Autre(String login, String nom, String prenom, String telephone, int niveauMin, int niveauMax,
            String activite) {
        super(login, nom, prenom, telephone, niveauMin, niveauMax);
        this.activite = activite;
    }

    public String getActivite() {
        return activite;
    }

    public void setActivite(String activite) {
        this.activite = activite;
    }

}
