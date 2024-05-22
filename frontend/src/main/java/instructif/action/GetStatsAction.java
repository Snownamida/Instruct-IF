package instructif.action;

import javax.servlet.http.HttpServletRequest;

import instructif.dto.StatDto;
import instructif.metier.modele.Matiere;

public class GetStatsAction extends AbstractAction {
    @Override
    public void execute(final HttpServletRequest request) {
        final Matiere populate_matiere = this.service.obtenirMatierePopulaire();

        final Long[] StatsSoutienTotal = this.service.obtenirStatsSoutienTotal();
        final Long nb_soutien = StatsSoutienTotal[0];
        final Long time_total_soutien = StatsSoutienTotal[1];

        final Long[] StatsSoutienEleve = this.service.obtenirStatsSoutienEleve();
        final Long nb_average_soutien_per_eleve = StatsSoutienEleve[0];
        final Long time_average_soutien_per_eleve = StatsSoutienEleve[1];

        final Long[] StatsSoutienEtablissement = this.service.obtenirStatsSoutienEtablissement();
        final Long nb_average_soutien_per_etab = StatsSoutienEtablissement[0];
        final Long time_average_soutien_per_etab = StatsSoutienEtablissement[1];

        final StatDto statDto = new StatDto(populate_matiere, nb_soutien, time_total_soutien,
                nb_average_soutien_per_eleve,
                time_average_soutien_per_eleve, nb_average_soutien_per_etab, time_average_soutien_per_etab);

        request.setAttribute("dto", statDto);
    }
}