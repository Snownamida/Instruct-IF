package instructif.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class GetLoginStateAction extends Action {
    @Override
    public void execute(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            request.setAttribute("logged-in", false);
            return;
        }

        Object user = session.getAttribute("user");
        if (user == null) {
            request.setAttribute("logged-in", false);
            return;
        }

        request.setAttribute("logged-in", true);
        request.setAttribute("user", user);
    }

}

