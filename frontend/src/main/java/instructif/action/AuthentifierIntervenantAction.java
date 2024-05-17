package instructif.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import instructif.metier.modele.Intervenant;

public class AuthentifierIntervenantAction extends AbstractAction {

    @Override
    public void execute(HttpServletRequest request) {
        // Authentification
        String login = request.getParameter("login");

        Intervenant i = this.service.connecterIntervenant(login);
        System.out.println("\n" + i);

        if (i != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("teacher", i);
        }

        request.setAttribute("utilisateur", i);
    }
}
