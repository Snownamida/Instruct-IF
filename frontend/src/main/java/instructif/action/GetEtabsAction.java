package instructif.action;

import javax.servlet.http.HttpServletRequest;

public class GetEtabsAction extends AbstractAction {
    @Override
    public void execute(HttpServletRequest request) {
        request.setAttribute("dto", this.service.obtenirListeEtablissements());
    }

}
