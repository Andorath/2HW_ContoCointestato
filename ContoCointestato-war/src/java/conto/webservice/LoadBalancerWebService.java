package conto.webservice;

import conto.facade.LoadBalancer;
import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 *
 * @author Damiano Di Stefano, Marco Giuseppe Salafia
 */
@WebService(serviceName = "LoadBalancerWebService")
public class LoadBalancerWebService
{

    @EJB
    private LoadBalancer loadBalancer;

    @WebMethod(operationName = "getCoordinator")
    public String getCoordinator()
    {
        return loadBalancer.getCoordinator();
    }
    
}
