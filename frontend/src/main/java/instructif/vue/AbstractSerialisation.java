package instructif.vue;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractSerialisation {
    abstract String serialize(HttpServletRequest request);

    abstract void setContentType(HttpServletResponse response);

    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setContentType(response);
        PrintWriter out = response.getWriter();
        out.println(serialize(request));
        out.close();
    }
}