package instructif.dto;

import java.util.Date;

import instructif.metier.modele.Demande;
import instructif.metier.modele.Eleve;
import instructif.metier.modele.Intervenant;
import instructif.metier.modele.Matiere;

public class DemandeDto {

    private transient Long id;
    private String description;
    private int evaluation;
    private String bilan;

    private Date dateDebut;
    private Date dateFin;
    private Long duree;

    private Boolean fini;

    private Matiere matiere;
    private Eleve eleve;
    private Intervenant intervenant;

    public DemandeDto(Demande demande) {
        this.id = demande.getId();
        this.description = demande.getDescription();
        this.evaluation = demande.getEvaluation();
        this.bilan = demande.getBilan();
        this.dateDebut = demande.getDateDebut();
        this.dateFin = demande.getDateFin();
        this.duree = demande.getDuree();
        this.fini = demande.getFini();
        this.matiere = demande.getMatiere();
        this.eleve = demande.getEleve();
        this.intervenant = demande.getIntervenant();
    }

    public DemandeDto(Long id, String description, int evaluation, String bilan, Date dateDebut, Date dateFin,
            Long duree, Boolean fini, Matiere matiere, Eleve eleve, Intervenant intervenant) {
        this.id = id;
        this.description = description;
        this.evaluation = evaluation;
        this.bilan = bilan;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.duree = duree;
        this.fini = fini;
        this.matiere = matiere;
        this.eleve = eleve;
        this.intervenant = intervenant;
    }

    @Override
    public String toString() {
        return "DemandeDto [id=" + id + ", description=" + description + ", evaluation=" + evaluation + ", bilan="
                + bilan + ", dateDebut=" + dateDebut + ", dateFin=" + dateFin + ", duree=" + duree + ", fini=" + fini
                + ", matiere=" + matiere + ", eleve=" + eleve + ", intervenant=" + intervenant + "]";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(int evaluation) {
        this.evaluation = evaluation;
    }

    public String getBilan() {
        return bilan;
    }

    public void setBilan(String bilan) {
        this.bilan = bilan;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
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

    public Matiere getMatiere() {
        return matiere;
    }

    public void setMatiere(Matiere matiere) {
        this.matiere = matiere;
    }

    public Eleve getEleve() {
        return eleve;
    }

    public void setEleve(Eleve eleve) {
        this.eleve = eleve;
    }

    public Intervenant getIntervenant() {
        return intervenant;
    }

    public void setIntervenant(Intervenant intervenant) {
        this.intervenant = intervenant;
    }

}
