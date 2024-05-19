package instructif.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import instructif.dto.EleveDTO;
import instructif.metier.modele.Eleve;

public class AuthentifierEleveAction extends AbstractAction {

    @Override
    public void execute(HttpServletRequest request) {
        // Authentification
        String mail = request.getParameter("mail");
        String password = request.getParameter("password");

        Eleve eleve = this.service.connecterEleve(mail, password);

        if (eleve != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", eleve);
            request.setAttribute("dto", new EleveDTO(eleve));
        }
    }
}