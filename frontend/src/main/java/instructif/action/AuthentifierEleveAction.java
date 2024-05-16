package instructif.action;

import javax.servlet.http.HttpServletRequest;

import instructif.metier.modele.Eleve;
import instructif.metier.service.Service;

public class AuthentifierEleveAction extends Action {

    @Override
    public void execute(HttpServletRequest request) {
        // Authentification
        String mail = request.getParameter("mail");
        String password = request.getParameter("password");

        Service service = new Service();

        Eleve e = service.connecterEleve(mail, password);
        System.out.println("\n" + e);

        request.setAttribute("utilisateur", e);
    }
}
