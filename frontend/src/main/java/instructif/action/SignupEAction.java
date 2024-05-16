package instructif.action;

import javax.servlet.http.HttpServletRequest;

import instructif.metier.modele.Eleve;
import instructif.metier.service.Service;


public class SignupEAction extends Action {
    @Override
    public void execute(HttpServletRequest request) {
        Service service = new Service();

        String nom = request.getParameter("lastName");
        String prenom = request.getParameter("firstName");
        String dateNaissance = request.getParameter("birthday");
        String classe = request.getParameter("class");
        String mail = request.getParameter("mail");
        String password = request.getParameter("password");
        String codeEtablissement = request.getParameter("codeEtablissement");

        Eleve eleve = new Eleve(nom, prenom, dateNaissance, Integer.parseInt(classe), mail, password);
        System.out.println(eleve);
        Boolean signUpStatus = service.inscrireEleve(eleve, codeEtablissement);

        request.setAttribute("signUpStatus", signUpStatus);
    }
}
