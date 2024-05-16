package instructif.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import instructif.action.AuthentifierEleveAction;
import instructif.action.AuthentifierIntervenantAction;
import instructif.action.SignupEAction;
import instructif.dao.JpaUtil;
import instructif.vue.ProfilEleveSerialisation;
import instructif.vue.ProfilIntervenantSerialisation;
import instructif.vue.SignUpStateSerialisation;

/**
 *
 * @author jsun
 */
@WebServlet(urlPatterns = { "/ActionServlet" })
public class ActionServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    public void init() throws ServletException {
        super.init();
        JpaUtil.creerFabriquePersistance();
    }

    @Override
    public void destroy() {
        JpaUtil.fermerFabriquePersistance();
        super.destroy();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("[TEST] Appel de l’ActionServlet ");
        String todo = request.getParameter("todo");

        switch (todo) {
            case "connecter-e":
                AuthentifierEleveAction authentifierEleveAction = new AuthentifierEleveAction();
                authentifierEleveAction.execute(request);
                ProfilEleveSerialisation profilEleveSerialisation = new ProfilEleveSerialisation();
                profilEleveSerialisation.serialize(request, response);
                break;

            case "connecter-i":
                AuthentifierIntervenantAction authentifierIntervenantAction = new AuthentifierIntervenantAction();
                authentifierIntervenantAction.execute(request);
                ProfilIntervenantSerialisation profilIntervenantSerialisation = new ProfilIntervenantSerialisation();
                profilIntervenantSerialisation.serialize(request, response);
                break;

            case "signup-e":
                SignupEAction signupE = new SignupEAction();
                signupE.execute(request);
                SignUpStateSerialisation signUpStateSerialisation = new SignUpStateSerialisation();
                signUpStateSerialisation.serialize(request, response);
                break;

            default:
                break;
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the
    // + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
