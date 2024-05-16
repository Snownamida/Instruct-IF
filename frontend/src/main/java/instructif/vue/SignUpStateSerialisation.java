package instructif.vue;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class SignUpStateSerialisation extends Serialisation {
    @Override
    public String serializeToJson(HttpServletRequest request) {
        JsonObject jsonObject = new JsonObject();

        Boolean signUpStatus = (Boolean) request.getAttribute("signUpStatus");
        jsonObject.addProperty("signUpStatus", signUpStatus);

        return new Gson().toJson(jsonObject);
    }

}
