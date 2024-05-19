package instructif.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import instructif.metier.modele.Intervenant;

public class StartVideoActionE extends AbstractAction {
    @Override
    public void execute(HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }

        final Object user = session.getAttribute("user");

        if (!(user instanceof Intervenant)) {
            return;
        }

        request.setAttribute("dto", this.service.lancerVisioIntervenant((Intervenant) user));

    }

}
