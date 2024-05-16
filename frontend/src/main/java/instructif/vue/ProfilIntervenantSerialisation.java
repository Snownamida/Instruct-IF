package instructif.vue;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import instructif.metier.modele.Intervenant;

public class ProfilIntervenantSerialisation extends Serialisation {
    @Override
    public void  serialize(HttpServletRequest request, HttpServletResponse response) {
        // Création d'un objet JSON contenant la date
        JsonObject jsonObject = new JsonObject();

        Intervenant intervenant = (Intervenant) request.getAttribute("utilisateur");
        if (intervenant != null) {
            jsonObject.addProperty("connexion", true);
            JsonObject intervenantJson = new JsonObject();
            intervenantJson.addProperty("id", intervenant.getId());
            intervenantJson.addProperty("nom", intervenant.getNom());
            intervenantJson.addProperty("prenom", intervenant.getPrenom());

            jsonObject.add("utilisateur", intervenantJson);
        } else {
            jsonObject.addProperty("connexion", false);
            jsonObject.add("utilisateur", null);
        }

        Gson gson = new Gson();
        String json = gson.toJson(jsonObject);
        System.out.println(json);

        response.setContentType("application/json;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.println(json);
            } catch (IOException ex) {
            Logger.getLogger(ProfilIntervenantSerialisation.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
