package instructif.vue;

import javax.servlet.http.HttpServletResponse;

public abstract class AbstractSerialisationJson extends AbstractSerialisation {

    @Override
    void setContentType(HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
    }

}