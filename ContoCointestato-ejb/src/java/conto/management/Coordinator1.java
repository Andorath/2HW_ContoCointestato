package conto.management;

import conto.payload.Proposal;
import conto.payload.ReadReply;
import javax.ejb.Local;

/**
 *
 * @author Damiano Di Stefano, Marco Giuseppe Salafia
 */
@Local
public interface Coordinator1
{
    void deposit(String userID, double operationValue);

    void withdraw(String userID, double operationValue);

    void submitProposal(Proposal p);

    void read(String userID);

    public void submitReadReply(ReadReply rr);

    String getReadResult();

    Boolean getWriteResult();  
}
