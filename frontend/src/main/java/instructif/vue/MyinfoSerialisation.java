package instructif.vue;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;

import instructif.dto.EleveDTO;
import instructif.metier.modele.Eleve;

public class MyinfoSerialisation extends Serialisation {
    @Override
    public String serializeToJson(HttpServletRequest request) {

        Object user = request.getAttribute("user");
        if (user instanceof Eleve) {
            EleveDTO eleveDTO = new EleveDTO((Eleve) user);
            return new Gson().toJson(eleveDTO);
        }
        return "{ \"logged-in\": false }";
    }

}
