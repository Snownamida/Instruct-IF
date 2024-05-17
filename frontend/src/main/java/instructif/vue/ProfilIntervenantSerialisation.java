package instructif.vue;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import instructif.metier.modele.Intervenant;

public class ProfilIntervenantSerialisation extends AbstractSerialisationJson {
    @Override
    public String serialize(HttpServletRequest request) {
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

        return new Gson().toJson(jsonObject);

    }
}
