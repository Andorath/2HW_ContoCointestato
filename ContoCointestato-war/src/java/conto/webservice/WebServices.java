package conto.webservice;

import conto.management.Coordinator;
import conto.rm.OperationRecord;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author Damiano Di Stefano, Marco Giuseppe Salafia
 */
@WebService(serviceName = "WebService")
public class WebServices
{
    @EJB
    private Coordinator coordinator;
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "read")
    public void read(@WebParam(name = "userID") String userID)
    {
        coordinator.read(userID);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "deposit")
    @Oneway
    public void deposit(@WebParam(name = "userID") String userID, 
                        @WebParam(name = "operationValue") double operationValue)
    {
        coordinator.deposit(userID, operationValue);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "withdraw")
    @Oneway
    public void withdraw(@WebParam(name = "userID") String userID, 
                         @WebParam(name = "operationValue") double operationValue)
    {
        coordinator.withdraw(userID, operationValue);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getReadResults")
    public String getReadResults()
    {
        //TODO write your implementation code here:
        return coordinator.getReadResult();
    }
}
