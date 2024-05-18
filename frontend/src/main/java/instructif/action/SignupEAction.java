package instructif.action;

import javax.servlet.http.HttpServletRequest;

import instructif.metier.modele.Eleve;

public class SignupEAction extends AbstractAction {
    @Override
    public void execute(HttpServletRequest request) {

        String nom = request.getParameter("lastName");
        String prenom = request.getParameter("firstName");
        String dateNaissance = request.getParameter("birthday");
        String classe = request.getParameter("class");
        String mail = request.getParameter("mail");
        String password = request.getParameter("password");
        String codeEtablissement = request.getParameter("codeEtablissement");

        Eleve eleve = new Eleve(nom, prenom, dateNaissance, Integer.parseInt(classe), mail, password);
        System.out.println(eleve);
        Boolean signUpStatus = this.service.inscrireEleve(eleve, codeEtablissement);

        request.setAttribute("dto", signUpStatus);
    }
}
