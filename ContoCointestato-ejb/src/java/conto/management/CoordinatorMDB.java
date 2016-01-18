package conto.management;

import conto.payload.Proposal;
import conto.payload.ReadReply;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 *
 * @author Damiano Di Stefano, Marco Giuseppe Salafia
 */
@MessageDriven(activationConfig =
{
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/RMQueue")
})
public class CoordinatorMDB implements MessageListener
{
    @EJB
    private Coordinator coordinatorBean;
    
    public CoordinatorMDB()
    {
    }
    
    @Override
    public void onMessage(Message message)
    {
        try
        {
            if (message instanceof ObjectMessage)
            {
                ObjectMessage objectMessage = (ObjectMessage) message;
                Object body = objectMessage.getBody(Object.class);
                
                if (body instanceof ReadReply)
                {
                    ReadReply rr = (ReadReply) body;
                    coordinatorBean.submitReadReply(rr); //quorum lettura
                    
                } else if (body instanceof Proposal)
                {
                    Proposal p = (Proposal) body;
                    coordinatorBean.submitProposal(p);  //quorum scrittura
                }
            }
            else
            {
                throw new Exception("FORMATO MESSAGGIO MALVAGIO!");
            }
            
        } catch (JMSException ex)
        {
            Logger.getLogger(CoordinatorMDB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex)
        {
            Logger.getLogger(CoordinatorMDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
