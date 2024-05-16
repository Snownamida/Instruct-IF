package instructif.vue;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class Serialisation {
    public abstract void serialize(HttpServletRequest request, HttpServletResponse response);
}