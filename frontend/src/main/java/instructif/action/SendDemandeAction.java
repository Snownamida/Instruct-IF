package instructif.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import instructif.dto.DemandeDto;
import instructif.metier.modele.Demande;
import instructif.metier.modele.Eleve;
import instructif.metier.modele.Matiere;

public class SendDemandeAction extends AbstractAction {
    @Override
    public void execute(final HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }

        final Object user = session.getAttribute("user");
        if (!(user instanceof Eleve)) {
            return;
        }

        final String matiereId = request.getParameter("matiereId");
        final String description = request.getParameter("description");
        if (matiereId == null || description == null) {
            return;
        }

        final List<Matiere> matieres = this.service.obtenirListeMatieres();
        final Matiere matiere = matieres.stream().filter(m -> m.getId().equals(Long.parseLong(matiereId))).findFirst()
                .orElse(null);

        if (matiere == null) {
            return;
        }

        Demande demande = this.service.envoyerDemande(matiere, description, (Eleve) user);
        if (demande == null) {
            return;
        }
        request.setAttribute("dto", new DemandeDto(demande));
    }
}