package conto.management;

import conto.payload.Proposal;
import conto.payload.ReadReply;
import conto.record.OperationRecord;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Damiano Di Stefano, Marco Giuseppe Salafia
 */
@Local
public interface Coordinator
{

    void deposit(String userID, double operationValue);

    void withdraw(String userID, double operationValue);

    void submitProposal(Proposal p);

    void read(String userID);

    public void submitReadReply(ReadReply rr);

    String getReadResult();
    
}
