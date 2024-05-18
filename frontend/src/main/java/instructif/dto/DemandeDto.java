package instructif.dto;

import java.util.Date;

import instructif.metier.modele.Demande;
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
    private EleveDto eleveDto;
    private IntervenantDto intervenantDto;

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
        
        if (demande.getEleve() != null) {
            this.eleveDto = new EleveDto(demande.getEleve());
        }
        if (demande.getIntervenant() != null) {
            this.intervenantDto = new IntervenantDto(demande.getIntervenant());
        }
    }

}
