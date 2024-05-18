package instructif.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import instructif.dto.IntervenantDto;
import instructif.metier.modele.Intervenant;

public class AuthentifierIntervenantAction extends AbstractAction {

    @Override
    public void execute(HttpServletRequest request) {
        // Authentification
        String login = request.getParameter("login");

        Intervenant intervenant = this.service.connecterIntervenant(login);

        if (intervenant != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", intervenant);
            request.setAttribute("dto", new IntervenantDto(intervenant));
        }

    }
}
