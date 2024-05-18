package instructif.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import instructif.metier.modele.Intervenant;
import instructif.metier.service.Service;

public class AuthentifierIntervenantAction extends Action {

    @Override
    public void execute(HttpServletRequest request) {
        // Authentification
        String login = request.getParameter("login");

        Service service = new Service();

        Intervenant i = service.connecterIntervenant(login);
        System.out.println("\n" + i);

        if (i != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("teacher", i);
        }

        request.setAttribute("utilisateur", i);
    }
}
