package instructif.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import instructif.dto.EleveDto;
import instructif.dto.IntervenantDto;
import instructif.metier.modele.Eleve;
import instructif.metier.modele.Intervenant;

public class GetMyInfoAction extends AbstractAction {
    @Override
    public void execute(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }

        Object user = session.getAttribute("user");

        if (user instanceof Eleve) {
            EleveDto eleveDTO = new EleveDto((Eleve) user);
            request.setAttribute("dto", eleveDTO);
            return;
        }

        if (user instanceof Intervenant) {
            IntervenantDto intervenantDTO = new IntervenantDto((Intervenant) user);
            request.setAttribute("dto", intervenantDTO);
            return;
        }

    }

}
