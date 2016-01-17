package conto.rm;

import javax.jms.Message;
import javax.jms.MessageListener;


/**
 *
 * @author Damiano Di Stefano, Marco Giuseppe Salafia
 */
public abstract class ReplicaManager implements MessageListener
{
    @Override
    public void onMessage(Message message)
    {
        processMessage(message);
    }
    
    public abstract void processMessage(Message message);
    
}
