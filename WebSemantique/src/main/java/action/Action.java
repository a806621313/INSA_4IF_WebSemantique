/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.servlet.http.HttpServletRequest;
import service.ServiceWebSeman;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author pacabrera
 */
public abstract class Action {
    ServiceWebSeman myService;
    public abstract void execute(HttpServletRequest request);
    public void setServiceMetier(ServiceWebSeman service){
        this.myService = service;
    }
}

