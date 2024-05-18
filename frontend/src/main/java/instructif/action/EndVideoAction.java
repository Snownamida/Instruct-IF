package instructif.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import instructif.metier.modele.Eleve;

public class EndVideoAction extends AbstractAction {
    @Override
    public void execute(final HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }

        final Object user = session.getAttribute("user");

        if (!(user instanceof Eleve)) {
            return;
        }

        request.setAttribute("dto", this.service.raccrocherEleve((Eleve) user));
    }

}
