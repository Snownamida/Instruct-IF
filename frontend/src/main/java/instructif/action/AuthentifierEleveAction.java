package instructif.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

        if (e != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("student", e);
        }

        request.setAttribute("utilisateur", e);
    }
}
