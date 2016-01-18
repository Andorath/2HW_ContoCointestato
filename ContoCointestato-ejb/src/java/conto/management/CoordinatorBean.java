package conto.management;

import conto.payload.Abort;
import conto.payload.Commitment;
import conto.payload.Operation;
import conto.payload.Proposal;
import conto.payload.Request;
import conto.payload.Read;
import conto.payload.ReadReply;
import conto.record.OperationRecord;

import java.io.Serializable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import javax.inject.Inject;
import javax.jms.Destination;

import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Damiano Di Stefano, Marco Giuseppe Salafia
 */
@Singleton
public class CoordinatorBean implements Coordinator
{
    @Resource(mappedName = "jms/RMTopic")
    private Topic rMTopic;
    
    @Resource
    private TimerService timeService;

    @Inject
    @JMSConnectionFactory("java:comp/DefaultJMSConnectionFactory")
    private JMSContext context;
        
    private int qW = 2;
    private int qR = 2;
    
    private LinkedList<Proposal> quorumWriteList;
    private LinkedList<ReadReply> quorumReadList;
    
    //il messageID della richiesta che sto processando
    private String processingRequest; 
    
    //risultato dell'operazione di lettura
    private List<OperationRecord> readResults;
    
    //risultato della scrittura
    private Boolean success = null;
    
    @PostConstruct
    public void initialize()
    {
        quorumWriteList = new LinkedList<>();
        quorumReadList = new LinkedList<>();
        readResults = new LinkedList<>();
    }
    
    private Message sendRequestMessage(Request r)
    {
        try
        {
            Destination d = InitialContext.doLookup("jms/RMQueue");
            r.setReplyTo(d);
            return sendJMSMessageToRMTopic(r);
        } 
        catch (NamingException ex)
        {
            Logger.getLogger(CoordinatorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    
    @Override
    public void read(String userID)
    {
        readResults.clear();
        
        Read r = new Read(userID);
        sendRequestMessage(r);
    }
    
    @Override
    public void deposit(String userID, double operationValue)
    {
        Operation o = new Operation(userID, Operation.OperationType.DEPOSIT, operationValue);
        success = null;
        
        try
        {
            processingRequest = sendRequestMessage(o).getJMSMessageID();
        } catch (JMSException ex)
        {
            Logger.getLogger(CoordinatorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void withdraw(String userID, double operationValue)
    {
        Operation o = new Operation(userID, Operation.OperationType.WITHDRAW, operationValue);
        success = null;
        
        try
        {
            processingRequest = sendRequestMessage(o).getJMSMessageID();
        } catch (JMSException ex)
        {
            Logger.getLogger(CoordinatorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendCommit(Commitment c)
    {
        sendJMSMessageToRMTopic(c);
    }
    
    private void sendAbort(Abort a)
    {
        sendJMSMessageToRMTopic(a);
    }

    private Message sendJMSMessageToRMTopic(Serializable s)
    {
        Message m = context.createObjectMessage(s);
        context.createProducer().send(rMTopic, s);
        return m;
    }    
    
    @Override
    public void submitReadReply(ReadReply rr)
    {
        if (quorumReadList.isEmpty())
        {
            quorumReadList.add(rr);
            timeService.createSingleActionTimer(1 * 1000, new TimerConfig("ReadQuorum", false));
        }
        else
        {
            quorumReadList.add(rr);
        }
    }

    @Override
    public void submitProposal(Proposal p)
    {
        if (quorumWriteList.isEmpty())
        {
            quorumWriteList.add(p);
            timeService.createSingleActionTimer(1 * 1000, new TimerConfig("WriteQuorum", false));
        }
        else
        {
            quorumWriteList.add(p);
        }
    }
    
    @Timeout
    private void timeoutQuorum(Timer t)
    {
        //TODO: REFACTOR...
        
        switch((String) t.getInfo())
        {
            
            case "ReadQuorum":
            {
                System.out.println("QUORUM LETTURA");
                if(isSatisfiedQuorum())
                {
                    createReadResult();
                }
                else
                {
                    System.out.println("QUORUM LETTURA FALLITO!");
                    
                    //per ritornare il risultato sbagliato al client
                    readResults.add(new OperationRecord(null, null, 0, 0));
                    quorumReadList.clear();
                }
         
                break;
            }
            
            case "WriteQuorum":
            {
                System.out.println("WRITE QUORUM");
                //invia il commit alle replica manager
                if(isSatisfiedQuorum())
                {
                    Commitment commit = createCommit();
                    sendCommit(commit);
                    success = true;
                }
                else
                {
                    Abort abort = createAbort();
                    sendAbort(abort);
                    success = false;
                }

                processingRequest = null;
                
                break;
            }
            
            default:
            {
                
            }
        }
    }  
    
    //data la politica FIFO nella gestione delle richieste
    //una delle due liste al momento dell'invocazione del metodo è sempre vuota.
    
    //TODO: meglio REFACTOR...
    private boolean isSatisfiedQuorum()
    {
        return (quorumWriteList.size() >= qW || quorumReadList.size() >= qR);
    }
    
    private Commitment createCommit()
    {
        Collections.sort(quorumWriteList);
        Proposal p = quorumWriteList.getLast();
        
        quorumWriteList.clear(); //tabula rasa
        
        return new Commitment(p.getMessageID(), p.getTimestamp());        
    }
    
    private Abort createAbort()
    {
        quorumWriteList.clear(); //tabula rasa
        
        return new Abort(processingRequest);
    }

    private void createReadResult()
    {
        Collections.sort(quorumReadList);
        readResults = quorumReadList.getLast().getOperations();
        
        quorumReadList.clear(); //tabula rasa
    }

    @Override
    public String getReadResult()
    {
        String result = "";
        
        if(!readResults.isEmpty() && readResults.get(0).getOperationID() == null)
        {
            return "LETTURA FALLITA!";
        }
        
        for(OperationRecord operation: readResults)
            result += operation + "\n";
        
        return result;
    }

    @Override
    public Boolean getWriteResult()
    {
        return (success != null) ? success : null;
    }
    
}
