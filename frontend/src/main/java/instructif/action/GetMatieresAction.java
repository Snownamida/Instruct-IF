package instructif.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import instructif.metier.modele.Matiere;

public class GetMatieresAction extends AbstractAction {
    @Override
    public void execute(HttpServletRequest request) {
        List<Matiere> matieres = this.service.obtenirListeMatieres();
        request.setAttribute("dto", matieres);
    }

}
