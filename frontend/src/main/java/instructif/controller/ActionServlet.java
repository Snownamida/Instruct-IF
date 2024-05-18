package instructif.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import instructif.action.AuthentifierEleveAction;
import instructif.action.AuthentifierIntervenantAction;
import instructif.action.EvaluateAction;
import instructif.action.GetMatieresAction;
import instructif.action.GetMyHistoryAction;
import instructif.action.GetMyInfoAction;
import instructif.action.GetStatsAction;
import instructif.action.SendDemandeAction;
import instructif.action.SignupEAction;
import instructif.action.StartVideoAction;
import instructif.dao.JpaUtil;
import instructif.vue.DtoSerialisationJson;

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

    protected void processRequest(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("[TEST] Appel de l’ActionServlet ");
        final String todo = request.getParameter("todo");

        switch (todo) {
            case "connecter-e":
                new AuthentifierEleveAction().execute(request);
                break;
            case "connecter-i":
                new AuthentifierIntervenantAction().execute(request);
                break;
            case "inscrire":
                new SignupEAction().execute(request);
                break;
            case "my-info":
                new GetMyInfoAction().execute(request);
                break;
            case "my-history":
                new GetMyHistoryAction().execute(request);
                break;
            case "get-matieres":
                new GetMatieresAction().execute(request);
                break;
            case "send-demande":
                new SendDemandeAction().execute(request);
                break;
            case "end-video":
                new EvaluateAction().execute(request);
                break;
            case "evaluate":
                new EvaluateAction().execute(request);
                break;
            case "current-demande":
                new GetMyInfoAction().execute(request);
                break;
            case "stats":
                new GetStatsAction().execute(request);
                break;
            case "etabs":
                new GetStatsAction().execute(request);
                break;
            case "start-video":
                new StartVideoAction().execute(request);
                break;
            case "send-bilan":
                new SendDemandeAction().execute(request);
            default:
                break;
        }
        new DtoSerialisationJson().execute(request, response);

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
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
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
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
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
