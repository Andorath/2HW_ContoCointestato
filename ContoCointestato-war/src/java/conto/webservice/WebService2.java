package conto.webservice;

import conto.management.Coordinator2;
import javax.ejb.EJB;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author Damiano Di Stefano, Marco Giuseppe Salafia
 */
@WebService(serviceName = "WebService2")
public class WebService2
{

    @EJB
    private Coordinator2 ejbRef;// Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Web Service Operation")

    @WebMethod(operationName = "deposit")
    @Oneway
    public void deposit(@WebParam(name = "userID") String userID, @WebParam(name = "operationValue") double operationValue)
    {
        ejbRef.deposit(userID, operationValue);
    }

    @WebMethod(operationName = "withdraw")
    @Oneway
    public void withdraw(@WebParam(name = "userID") String userID, @WebParam(name = "operationValue") double operationValue)
    {
        ejbRef.withdraw(userID, operationValue);
    }

    @WebMethod(operationName = "read")
    @Oneway
    public void read(@WebParam(name = "userID") String userID)
    {
        ejbRef.read(userID);
    }

    @WebMethod(operationName = "getReadResult")
    public String getReadResult()
    {
        return ejbRef.getReadResult();
    }

    @WebMethod(operationName = "getWriteResult")
    public Boolean getWriteResult()
    {
        return ejbRef.getWriteResult();
    }
    
}
