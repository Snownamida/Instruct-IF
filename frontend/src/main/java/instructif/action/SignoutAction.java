package instructif.action;

import javax.servlet.http.HttpServletRequest;

public class SignoutAction extends AbstractAction {
    @Override
    public void execute(HttpServletRequest request) {
        request.getSession().invalidate();
    }
}