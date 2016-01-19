package conto.rm;

import conto.payload.Abort;
import conto.payload.Commitment;
import conto.payload.Request;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;

import javax.jms.JMSException;
import javax.jms.Message;

import javax.xml.ws.WebServiceRef;

/**
 *
 * @author Damiano Di Stefano, Marco Giuseppe Salafia
 */
@MessageDriven(activationConfig =
{
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/RMTopic")
})
public class RM2 extends ReplicaManager
{

    @EJB
    private RM2QueueHandlerLocal handler;
    
    public RM2()
    {
    }
    
    @Override
    public void processMessage(Message message)
    {
        
        try
        {             
            Object body = message.getBody(Object.class);
            
            if (body instanceof Request)
            {
                Request op = (Request) body;
                handler.enqueue(message.getJMSMessageID(), op);
            }
            else if (body instanceof Commitment)
            {
                Commitment c = (Commitment) body;
                handler.commit(c);
            }
            
            else if (body instanceof Abort)
            {
                Abort a = (Abort) body;
                handler.abort(a);
                System.err.print("ABORT.");
            }
            
        } 
        catch (JMSException ex)
        {
            Logger.getLogger(RM1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    
}
