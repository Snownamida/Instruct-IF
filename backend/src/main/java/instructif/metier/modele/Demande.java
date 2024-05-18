/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package instructif.metier.modele;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author mbaratova
 */

// POJO = Plain Old Java Object = Classe basique

@Entity // Au début ça met une erreur. Il faut clic droit -> "fix imports" pour importer
        // tout le nécessaire
public class Demande {

    // Les noms de types prennent des majuscules
    // Tout est en private !
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String description;
    private int evaluation; // 1 = rien compris, 2 = moyennement compris, 3 = tout compris
    private String bilan;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDebut;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateFin;
    private Long duree; // temps en secondes

    private Boolean fini; // true si la demande est complete, tous les champs sont remplis

    @ManyToOne
    private Matiere matiere;
    @ManyToOne
    private Eleve eleve;
    @ManyToOne
    private Intervenant intervenant;

    @Override
    public String toString() {
        return "Demande [id=" + id + ", description=" + description + ", evaluation=" + evaluation + ", bilan=" + bilan
                + ", dateDebut=" + dateDebut + ", dateFin=" + dateFin + ", duree=" + duree + ", fini=" + fini
                + ", matiere=" + matiere + ", eleve=" + eleve + ", intervenant=" + intervenant + "]";
    }

    // On peut générer les constructeurs et getter/setter automatiquement avec clic
    // drout + Insert code
    // null par défaut dans les attributs
    public Demande() {
    } // ON DOIT mettre un constructeur sans paramètre !!

    public Demande(String description, Matiere matiere, Eleve eleve) {
        this.description = description;
        this.matiere = matiere;
        this.eleve = eleve;
        this.fini = false;
    }

    public String obtenirDateFormatee(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy | HH:mmm:ss");
        String dateFormatee = format.format(date);
        return dateFormatee;

    }

    public String getDescription() {
        return description;
    }

    public int getEvaluation() {
        return evaluation;
    }

    public String getBilan() {
        return bilan;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEvaluation(int evaluation) {
        this.evaluation = evaluation;
    }

    public void setBilan(String bilan) {
        this.bilan = bilan;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public Matiere getMatiere() {
        return matiere;
    }

    public Eleve getEleve() {
        return eleve;
    }

    public void setMatiere(Matiere matiere) {
        this.matiere = matiere;
    }

    public void setEleve(Eleve eleve) {
        this.eleve = eleve;
    }

    public Long getId() {
        return id;
    }

    public Intervenant getIntervenant() {
        return intervenant;
    }

    public void setIntervenant(Intervenant intervenant) {
        this.intervenant = intervenant;
    }

    public Long getDuree() {
        return duree;
    }

    public void setDuree(Long duree) {
        this.duree = duree;
    }

    public Boolean getFini() {
        return fini;
    }

    public void setFini(Boolean fini) {
        this.fini = fini;
    }

}
