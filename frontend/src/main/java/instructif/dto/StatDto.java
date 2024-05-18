package instructif.dto;

import instructif.metier.modele.Matiere;

public class StatDto {
    private Matiere populate_matiere;
    private Long nb_soutien;
    private Long time_total_soutien;
    private Long nb_average_soutien_per_eleve;
    private Long time_average_soutien_per_eleve;
    private Long nb_average_soutien_per_etab;
    private Long time_average_soutien_per_etab;

    public StatDto(Matiere populate_matiere, Long nb_soutien, Long time_total_soutien,
            Long nb_average_soutien_per_eleve, Long time_average_soutien_per_eleve, Long nb_average_soutien_per_etab,
            Long time_average_soutien_per_etab) {
        this.populate_matiere = populate_matiere;
        this.nb_soutien = nb_soutien;
        this.time_total_soutien = time_total_soutien;
        this.nb_average_soutien_per_eleve = nb_average_soutien_per_eleve;
        this.time_average_soutien_per_eleve = time_average_soutien_per_eleve;
        this.nb_average_soutien_per_etab = nb_average_soutien_per_etab;
        this.time_average_soutien_per_etab = time_average_soutien_per_etab;
    }

}
