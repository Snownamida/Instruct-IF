/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package instructif.metier.modele;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 *
 * @author mbaratova
 */
@Entity
@Inheritance (strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type_intervenant")
public class Intervenant {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)    
    protected Long id;
    protected String login;
    protected String nom;
    protected String prenom;
    protected String telephone;
    protected int niveauMin;
    protected int niveauMax;
    protected int nombreInterventions;
    protected Boolean disponible;

    public Intervenant() {
    }

    public Intervenant(String login, String nom, String prenom, String telephone, int niveauMin, int niveauMax) {
        this.login = login;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.niveauMin = niveauMin;
        this.niveauMax = niveauMax;
        
        this.nombreInterventions = 0;
        this.disponible = true;
    }

    @Override
    public String toString() {
        return "Intervenant #" + id + " : " + nom + " " + prenom + ". Tél : " + telephone + ". Classes d'enseignement : " + niveauMin + "-" + niveauMax
                + ". Disponibilité actuelle : " + disponible + ". Nombre d'interventions : " + nombreInterventions + ". Login : " + login;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getTelephone() {
        return telephone;
    }


    public int getNombreInterventions() {
        return nombreInterventions;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }


    public void setNombreInterventions(int nombreInterventions) {
        this.nombreInterventions = nombreInterventions;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public int getNiveauMin() {
        return niveauMin;
    }

    public void setNiveauMin(int niveauMin) {
        this.niveauMin = niveauMin;
    }

    public int getNiveauMax() {
        return niveauMax;
    }

    public void setNiveauMax(int niveauMax) {
        this.niveauMax = niveauMax;
    }
    
    
    
}
