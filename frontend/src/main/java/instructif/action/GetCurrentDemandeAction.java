package instructif.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import instructif.dto.DemandeDto;
import instructif.metier.modele.Demande;
import instructif.metier.modele.Intervenant;

public class GetCurrentDemandeAction extends AbstractAction {
    @Override
    public void execute(HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }

        final Object user = session.getAttribute("user");

        if (!(user instanceof Intervenant)) {
            return;
        }

        Demande demande = this.service.obtenirDemandeIntervenant((Intervenant) user);
        if (demande == null) {
            return;
        }

        request.setAttribute("dto", new DemandeDto(demande));

    }

}
