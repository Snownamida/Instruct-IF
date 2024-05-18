package instructif.vue;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;

public class DtoSerialisationJson extends AbstractSerialisationJson {
    @Override
    public String serialize(HttpServletRequest request) {
        Object dto = request.getAttribute("dto");
        return new Gson().toJson(dto);
    }

}
