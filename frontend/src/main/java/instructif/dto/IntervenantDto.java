package instructif.dto;

import instructif.metier.modele.Autre;
import instructif.metier.modele.Enseignant;
import instructif.metier.modele.Etudiant;
import instructif.metier.modele.Intervenant;

public class IntervenantDto {
    private Long id;
    private String login;
    private String nom;
    private String prenom;
    private String telephone;
    private int niveauMin;
    private int niveauMax;
    private int nombreInterventions;
    private Boolean disponible;

    private String type = "Intervenant";

    // Etudiant
    private String universite;
    private String specialite;

    // Enseignant
    private String typeEtablissementExercice;

    // Autre
    private String activite;

    public IntervenantDto(Intervenant intervenant) {
        this.id = intervenant.getId();
        this.login = intervenant.getLogin();
        this.nom = intervenant.getNom();
        this.prenom = intervenant.getPrenom();
        this.telephone = intervenant.getTelephone();
        this.niveauMin = intervenant.getNiveauMin();
        this.niveauMax = intervenant.getNiveauMax();
        this.nombreInterventions = intervenant.getNombreInterventions();
        this.disponible = intervenant.getDisponible();

        if (intervenant instanceof Etudiant) {
            this.type = "Etudiant";
            this.universite = ((Etudiant) intervenant).getUniversite();
            this.specialite = ((Etudiant) intervenant).getSpecialite();
        } else if (intervenant instanceof Enseignant) {
            this.type = "Enseignant";
            this.typeEtablissementExercice = ((Enseignant) intervenant)
                    .getTypeEtablissementExercice();
        } else if (intervenant instanceof Autre) {
            this.type = "Autre";
            this.activite = ((Autre) intervenant).getActivite();
        }
    }

    @Override
    public String toString() {
        return "IntervenantDto [id=" + id + ", login=" + login + ", nom=" + nom + ", prenom=" + prenom + ", telephone="
                + telephone + ", niveauMin=" + niveauMin + ", niveauMax=" + niveauMax + ", nombreInterventions="
                + nombreInterventions + ", disponible=" + disponible + ", type=" + type + ", universite=" + universite
                + ", specialite=" + specialite + ", typeEtablissementExercice=" + typeEtablissementExercice
                + ", activite=" + activite + "]";
    }

}
