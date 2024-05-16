package instructif.vue;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import instructif.metier.modele.Eleve;

public class ProfilEleveSerialisation extends Serialisation {
    @Override
    public String serializeToJson(HttpServletRequest request) {
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

        return new Gson().toJson(jsonObject);

    }
}
