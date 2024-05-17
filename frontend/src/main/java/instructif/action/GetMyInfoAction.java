package instructif.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import instructif.dto.EleveDTO;
import instructif.metier.modele.Eleve;

public class GetMyInfoAction extends AbstractAction {
    @Override
    public void execute(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        Object user = session.getAttribute("user");

        if (user instanceof Eleve) {
            EleveDTO eleveDTO = new EleveDTO((Eleve) user);
            request.setAttribute("dto", eleveDTO);
            return;
        }

    }

}
