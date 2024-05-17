/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package instructif.metier.modele;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author mbaratova
 */

// POJO = Plain Old Java Object = Classe basique

@Entity // Au début ça met une erreur. Il faut clic droit -> "fix imports" pour importer
        // tout le nécessaire
public class Eleve {
    // Les noms de types prennent des majuscules
    // Tout est en private !
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nom;
    private String prenom;

    @Column(unique = true)
    private String mail;
    private String motDePasse;
    private String dateNaissance;
    private int classe;

    @ManyToOne
    private Etablissement etablissement;

    // Tip : On peut générer les constructeurs et getter/setter automatiquement avec
    // clic drout + Insert code
    // Tip : null par défaut dans les attributs

    @Override
    public String toString() {
        return "Eleve [id=" + id + ", nom=" + nom + ", prenom=" + prenom + ", mail=" + mail + ", motDePasse="
                + motDePasse + ", dateNaissance=" + dateNaissance + ", classe=" + classe + ", etablissement="
                + etablissement + "]";
    }

    public Eleve() {
    } // Tip: ON DOIT mettre un constructeur sans paramètre !!

    public Eleve(String nom, String prenom, String dateNaissance, int classe, String mail, String motDePasse) {
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.motDePasse = motDePasse;
        this.dateNaissance = dateNaissance;
        this.classe = classe;
    }

    public String obtenirDenominationClasse() {
        String denomination;
        switch (classe) {
            case 0:
                denomination = "Terminale";
                break;
            case 1:
                denomination = "Première";
                break;
            case 2:
                denomination = "Seconde";
                break;
            default:
                denomination = classe + "ème";

        }
        return denomination;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getMail() {
        return mail;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public int getClasse() {
        return classe;
    }

    public Etablissement getEtablissement() {
        return etablissement;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public void setClasse(int classe) {
        this.classe = classe;
    }

    public void setEtablissement(Etablissement etablissement) {
        this.etablissement = etablissement;
    }
}
