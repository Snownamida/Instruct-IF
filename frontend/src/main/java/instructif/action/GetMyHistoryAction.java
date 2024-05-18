package instructif.action;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import instructif.dto.DemandeDto;
import instructif.metier.modele.Demande;
import instructif.metier.modele.Eleve;
import instructif.metier.modele.Intervenant;

public class GetMyHistoryAction extends AbstractAction {
    @Override
    public void execute(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }

        Object user = session.getAttribute("user");

        if (user instanceof Eleve) {
            List<Demande> demandes = this.service.obtenirHistoriqueEleve((Eleve) user);
            List<DemandeDto> demandeDtos = demandes.stream().map(DemandeDto::new).collect(Collectors.toList());
            request.setAttribute("dto", demandeDtos);
            return;
        }

        if (user instanceof Intervenant) {
            List<Demande> demandes = this.service.obtenirHistoriqueIntervenant((Intervenant) user);
            List<DemandeDto> demandeDtos = demandes.stream().map(DemandeDto::new).collect(Collectors.toList());
            request.setAttribute("dto", demandeDtos);
            return;
        }
    }

}
