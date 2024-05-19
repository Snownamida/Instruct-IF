package instructif.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import instructif.metier.modele.Intervenant;

public class SendBilanAction extends AbstractAction {
    @Override
    public void execute(final HttpServletRequest request) {

        final String bilan = request.getParameter("bilan");
        if (bilan == null) {
            return;
        }

        final HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }

        final Object user = session.getAttribute("user");

        if (!(user instanceof Intervenant)) {
            return;
        }

        request.setAttribute("dto", this.service.envoyerBilan((Intervenant) user, bilan));

    }

}
