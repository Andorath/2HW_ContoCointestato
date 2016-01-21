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
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/RM2Queue")
})
public class Coordinator2MDB implements MessageListener
{

    @EJB
    private Coordinator2 coordinatorBean;
    
    
    
    public Coordinator2MDB()
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
                    System.out.println("\n\n[COO1] Ricevuta proposta -> " + ((Proposal) body).getTimestamp() + 
                            " per la richiesta -> "+ ((Proposal) body).getMessageID() + "\n\n");
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
            Logger.getLogger(Coordinator1MDB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex)
        {
            Logger.getLogger(Coordinator1MDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
