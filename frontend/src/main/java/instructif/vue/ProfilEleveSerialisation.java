package instructif.vue;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import instructif.metier.modele.Eleve;

public class ProfilEleveSerialisation extends Serialisation {
    @Override
    public void  serialize(HttpServletRequest request, HttpServletResponse response) {
        // Création d'un objet JSON contenant la date
        JsonObject jsonObject = new JsonObject();

        Eleve eleve = (Eleve) request.getAttribute("utilisateur");
        if (eleve != null) {
            jsonObject.addProperty("connexion", true);
            JsonObject eleveJson = new JsonObject();
            eleveJson.addProperty("id", eleve.getId());
            eleveJson.addProperty("nom", eleve.getNom());
            eleveJson.addProperty("prenom", eleve.getPrenom());
            eleveJson.addProperty("mail", eleve.getMail());

            jsonObject.add("utilisateur", eleveJson);
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
            Logger.getLogger(ProfilEleveSerialisation.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
