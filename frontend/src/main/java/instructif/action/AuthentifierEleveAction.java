package instructif.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import instructif.metier.modele.Eleve;

public class AuthentifierEleveAction extends AbstractAction {

    @Override
    public void execute(HttpServletRequest request) {
        // Authentification
        String mail = request.getParameter("mail");
        String password = request.getParameter("password");

        Eleve e = this.service.connecterEleve(mail, password);

        if (e != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", e);
        }
        System.out.println(e);

        request.setAttribute("utilisateur", e);
    }
}
