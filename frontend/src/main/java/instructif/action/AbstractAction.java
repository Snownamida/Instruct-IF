package instructif.action;

import javax.servlet.http.HttpServletRequest;

import instructif.metier.service.Service;

public abstract class AbstractAction {
    Service service = new Service();

    public abstract void execute(HttpServletRequest request);
}