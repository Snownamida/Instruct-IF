package instructif.action;

import javax.servlet.http.HttpServletRequest;

import instructif.metier.modele.Intervenant;
import instructif.metier.service.Service;

public class AuthentifierIntervenantAction extends Action {

    @Override
    public void execute(HttpServletRequest request) {
        // Authentification
        String login = request.getParameter("login");

        Service service = new Service();

        System.out.println(login);
        Intervenant i = service.connecterIntervenant(login);
        System.out.println("\n" + i);

        request.setAttribute("utilisateur", i);
    }
}
