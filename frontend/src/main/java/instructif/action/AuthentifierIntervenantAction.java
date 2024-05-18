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

<<<<<<< HEAD
        Intervenant i = service.connecterIntervenant(login);
        System.out.println("\n" + i);

        if (i != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("teacher", i);
        }

        request.setAttribute("utilisateur", i);
=======
        if (intervenant != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", intervenant);
            request.setAttribute("dto", new IntervenantDto(intervenant));
        }

>>>>>>> 2cf4c1523bb7ddefd267b0ac72aa8619355994ea
    }
}
