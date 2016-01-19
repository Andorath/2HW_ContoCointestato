package conto.rm;

import conto.payload.Abort;
import conto.payload.Commitment;
import conto.payload.Request;

import javax.ejb.Local;

/**
 *
 * @author Damiano Di Stefano, Marco Giuseppe Salafia
 */
@Local
public interface RM3QueueHandlerLocal
{
    void enqueue(String messageID, Request request);

    void commit(Commitment c);

    void abort(Abort a);
    
}
