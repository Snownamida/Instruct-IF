/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package instructif.metier.modele;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import instructif.util.EducNetApi;

/**
 *
 * @author mbaratova
 */
@Entity
public class Etablissement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    String code;
    String nom;
    String secteur;
    String nomCommune;
    String codeDepartement;
    String nomDepartement;
    String ips;
    Long nbDemandes;
    Long dureeMoyenne;

    
    public Etablissement(){
    }       

    public Etablissement(String code, String nom, String secteur, String nomCommune, String codeDepartement, String nomDepartement, String ips) {
        this.code = code;
        this.nom = nom;
        this.secteur = secteur;
        this.nomCommune = nomCommune;
        this.codeDepartement = codeDepartement;
        this.nomDepartement = nomDepartement;
        this.ips = ips;
        this.nbDemandes = (long) 0;
        this.dureeMoyenne = (long) 0;
    }

    @Override
    public String toString() {
        return "Etablissement #" + id + ", code : " + code + ", Nom :" + nom + " ,"+ secteur + " dans " + nomCommune + ", " + codeDepartement + " " + nomDepartement + ". IPS : " + ips
                + ". Soutiens demandées : " + nbDemandes + ", durée moyenne d'un soutien :" + dureeMoyenne + "s.";
    }

    public Long getId() {
        return id;
    }

    public String getSecteur() {
        return secteur;
    }

    public String getNomCommune() {
        return nomCommune;
    }

    public String getNomDepartement() {
        return nomDepartement;
    }

    public Long getNbDemandes() {
        return nbDemandes;
    }

    public void setNbDemandes(Long nbDemandes) {
        this.nbDemandes = nbDemandes;
    }

    public Long getDureeMoyenne() {
        return dureeMoyenne;
    }

    public void setDureeMoyenne(Long dureeMoyenne) {
        this.dureeMoyenne = dureeMoyenne;
    }

    public String getCode() {
        return code;
    }

    public String getNom() {
        return nom;
    }

    public String getCodeDepartement() {
        return codeDepartement;
    }

    public String getIps() {
        return ips;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setSecteur(String secteur) {
        this.secteur = secteur;
    }

    public void setNomCommune(String nomCommune) {
        this.nomCommune = nomCommune;
    }

    public void setCodeDepartement(String codeDepartement) {
        this.codeDepartement = codeDepartement;
    }

    public void setNomDepartement(String nomDepartement) {
        this.nomDepartement = nomDepartement;
    }

    public void setIps(String ips) {
        this.ips = ips;
    }
    
    
    
    
    
}
