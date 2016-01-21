package conto.management;

import conto.payload.Abort;
import conto.payload.Commitment;
import conto.payload.Operation;
import conto.payload.Proposal;
import conto.payload.Request;
import conto.payload.Read;
import conto.payload.ReadReply;
import conto.record.OperationRecord;
import fault.ws.FaultDetectorWS_Service;

import java.io.Serializable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;

import javax.ejb.Singleton;
import javax.ejb.Startup;
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
import javax.xml.ws.WebServiceRef;

/**
 *
 * @author Damiano Di Stefano, Marco Giuseppe Salafia
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Startup
public class Coordinator1Bean implements Coordinator1
{

    @WebServiceRef(wsdlLocation = "META-INF/wsdl/172.16.105.185_8080/FaulDetector-war/FaultDetectorWS.wsdl")
    private FaultDetectorWS_Service service;
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
        timeService.createIntervalTimer(10, 1000, new TimerConfig("Keep Alive", false));
    }
    
    @PreDestroy
    public void destroy()
    {
        timeService.getAllTimers().clear();
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
            Logger.getLogger(Coordinator1Bean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    @Lock(LockType.WRITE)
    @Override
    public void read(String userID)
    {
        readResults.clear();
        
        Read r = new Read(userID);
        sendRequestMessage(r);
    }
    
    @Lock(LockType.WRITE)
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
            Logger.getLogger(Coordinator1Bean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Lock(LockType.WRITE)
    @Override
    public void withdraw(String userID, double operationValue)
    {
        System.out.println("[COO1] Chiamato WithDraw");
        Operation o = new Operation(userID, Operation.OperationType.WITHDRAW, operationValue);
        success = null;
        
        try
        {
            processingRequest = sendRequestMessage(o).getJMSMessageID();
        } catch (JMSException ex)
        {
            Logger.getLogger(Coordinator1Bean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendCommit(Commitment c)
    {
        System.out.println("[COO1] Invio Commit -> " + c.getTimestamp());
        sendJMSMessageToRMTopic(c);
    }
    
    private void sendAbort(Abort a)
    {
        sendJMSMessageToRMTopic(a);
    }

    private Message sendJMSMessageToRMTopic(Serializable s)
    {
        Message m = context.createObjectMessage(s);
        context.createProducer().send(rMTopic, m);
        return m;
    }    
    
    @Lock(LockType.WRITE)
    @Override
    public void submitReadReply(ReadReply rr)
    {
        if (quorumReadList.isEmpty())
        {
            quorumReadList.add(rr);
            timeService.createSingleActionTimer(300, new TimerConfig("ReadQuorum", false));
        }
        else
        {
            quorumReadList.add(rr);
        }
    }

    @Lock(LockType.WRITE)
    @Override
    public void submitProposal(Proposal p)
    {
        if(p.getMessageID().equalsIgnoreCase(processingRequest))
        {
            if (quorumWriteList.isEmpty())
            {
                quorumWriteList.add(p);
                timeService.createSingleActionTimer(300, new TimerConfig("WriteQuorum", false));
            }
            else
            {
                quorumWriteList.add(p);
            }
        }
        else
        {
            System.out.println("[COO1] Proposta arrivata in ritardo -> " + p.getTimestamp()
                    + " Sto processando la richiesta -> " + processingRequest);
        }
    }
    
    @Lock(LockType.WRITE)
    @Timeout
    private void timeoutQuorum(Timer t)
    {
        //TODO: REFACTOR...
        
        switch((String) t.getInfo())
        {
            
            case "ReadQuorum":
            {
                System.out.println("[COO1] QUORUM LETTURA");
                if(isSatisfiedQuorum())
                {
                    createReadResult();
                }
                else
                {
                    System.out.println("[COO1] QUORUM LETTURA FALLITO!");
                    
                    //per ritornare il risultato sbagliato al client
                    readResults.add(new OperationRecord(null, null, 0, 0));
                    quorumReadList.clear();
                }
         
                break;
            }
            
            case "WriteQuorum":
            {
                //invia il commit alle replica manager
                if(isSatisfiedQuorum())
                {
                    System.out.println("[COO1] WRITE QUORUM SUCCESS");
                    
                    Commitment commit = createCommit();
                    sendCommit(commit);
                    success = true;
                }
                else
                {
                    System.out.println("[COO1] WRITE QUORUM ABORT");
                    
                    Abort abort = createAbort();
                    sendAbort(abort);
                    success = false;
                }

                processingRequest = null;
                
                break;
            }
            
            case "Keep Alive":
            {
                keepAlive("COO1");
            }
            
            default:
            {
                
            }
        }
    }  
    
    //data la politica FIFO nella gestione delle richieste
    //una delle due liste al momento dell'invocazione del metodo è sempre vuota.
    
    //TODO: meglio REFACTOR...
    @Lock(LockType.READ)
    private boolean isSatisfiedQuorum()
    {
        return (quorumWriteList.size() >= qW || quorumReadList.size() >= qR);
    }
    
    @Lock(LockType.WRITE)
    private Commitment createCommit()
    {
        Collections.sort(quorumWriteList);
        Proposal p = quorumWriteList.getLast();
        
        quorumWriteList.clear(); //tabula rasa
        
        return new Commitment(p.getMessageID(), p.getTimestamp());        
    }
    
    @Lock(LockType.WRITE)
    private Abort createAbort()
    {
        quorumWriteList.clear(); //tabula rasa
        
        return new Abort(processingRequest);
    }

    @Lock(LockType.WRITE)
    private void createReadResult()
    {
        Collections.sort(quorumReadList);
        readResults = quorumReadList.getLast().getOperations();
        
        if(readResults.isEmpty())
        {
            readResults.add(new OperationRecord("VUOTO", null, 0, 0));
        }
        
        quorumReadList.clear(); //tabula rasa
    }

    @Lock(LockType.READ)
    @Override
    public String getReadResult()
    {
        System.out.println("[COO1] Richiesta di risultato lettura");
        String result = "";
        
        if(!readResults.isEmpty() && readResults.get(0).getOperationID() == null)
        {
            return "LETTURA FALLITA!";
        } else if (!readResults.isEmpty() && readResults.get(0).getOperationID().equalsIgnoreCase("VUOTO"))
        {
            return "Nessuna Operazione.";
        }
        
        for(OperationRecord operation: readResults)
            result += operation + "\n";
        
        return result;
    }

    @Lock(LockType.READ)
    @Override
    public Boolean getWriteResult()
    {
        return (success != null) ? success : null;
    }

    private void keepAlive(java.lang.String processID)
    {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        fault.ws.FaultDetectorWS port = service.getFaultDetectorWSPort();
        port.keepAlive(processID);
    }
    
    
    
}
