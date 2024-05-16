package instructif.vue;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class SignUpStateSerialisation extends Serialisation {
    @Override
    public void serialize(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject jsonObject = new JsonObject();

        Boolean signUpStatus = (Boolean) request.getAttribute("signUpStatus");
        jsonObject.addProperty("signUpStatus", signUpStatus);

        String json = new Gson().toJson(jsonObject);

        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println(json);
        }
    }

}
