package conto.rm;

import conto.payload.Abort;
import conto.payload.Commitment;
import conto.payload.Operation;
import conto.payload.Proposal;
import conto.payload.Read;
import conto.payload.ReadReply;
import conto.payload.Request;

import conto.record.OperationRecord;
import conto.record.PendingRequest;

import conto.timestamp.RMTimestamp;
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
import javax.jms.Queue;

import javax.xml.ws.WebServiceRef;

/**
 *
 * @author Damiano Di Stefano, Marco Giuseppe Salafia
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Startup
public class RM2QueueHandler implements RM2QueueHandlerLocal
{

    @WebServiceRef(wsdlLocation = "META-INF/wsdl/172.16.105.185_8080/FaulDetector-war/FaultDetectorWS.wsdl")
    private FaultDetectorWS_Service service_2;

    @WebServiceRef(wsdlLocation = "META-INF/wsdl/localhost_8080/Entity2/Entity2WebService.wsdl")
    private conto.ws.Entity2WebService_Service service;

    @Resource(mappedName = "jms/RMQueue")
    private Queue rMQueue;

    @Inject
    @JMSConnectionFactory("java:comp/DefaultJMSConnectionFactory")
    private JMSContext context;
    
    @Resource 
    private TimerService ts;
    
    private Timer timer;
    
    private LinkedList<PendingRequest> pendingQueue;
    private RMTimestamp timestamp;
    private RMTimestamp replicaVersion;
    
    @PostConstruct
    private void initialize()
    {
        pendingQueue = new LinkedList<>();
        timestamp = new RMTimestamp(0, "RM2");
        replicaVersion = new RMTimestamp(0, "");
        timer = ts.createIntervalTimer(10, 1000, new TimerConfig());
    }

    @PreDestroy
    public void destroy()
    {
        ts.getAllTimers().clear();
    }
    
    @Timeout
    private void sendAlive()
    {
        keepAlive("RM2");
    }

    @Lock(LockType.WRITE)
    @Override
    public void enqueue(String messageID, Request request) 
    {
        try
        {
            timestamp.incrementCounter();
            
            RMTimestamp requestTimestamp = (RMTimestamp) timestamp.clone();
            
            PendingRequest pr = new PendingRequest(messageID, requestTimestamp, request);
            pendingQueue.add(pr);
            Collections.sort(pendingQueue);
            
            //Se è scrittura mando la mia proposta
            if(request instanceof Operation)
            {
                Proposal myProposal = new Proposal(messageID, requestTimestamp);
                sendJMSMessageToRMQueue(request.getReplyTo(), myProposal);
            }
            else if(request instanceof Read)
            {
                tryDelivery();
            }
        } 
        catch (CloneNotSupportedException ex)
        {
            Logger.getLogger(RM1QueueHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Lock(LockType.READ)
    private PendingRequest getRequestByMessaggeID(String messageID)
    {
        for(PendingRequest pr: pendingQueue)
            if (pr.getMessageID().equalsIgnoreCase(messageID))
                return pr;
        
        return null;
    }

    @Lock(LockType.WRITE)
    @Override
    public void abort(Abort a)
    {
        PendingRequest pr = getRequestByMessaggeID(a.getMessageID());
        
        if(pr != null)
        {
            pendingQueue.remove(pr);
        }
    }
    
    @Lock(LockType.READ)
    private String stampaPendingQueue()
    {
        String result = "\n";
        for(PendingRequest pr: pendingQueue)
        {
            result += pr + "\n";
        }
        
        return result;
    }
    
    @Lock(LockType.WRITE)
    @Override
    public void commit(Commitment c)
    {        
        PendingRequest pr = getRequestByMessaggeID(c.getMessageID());
        
        if(pr != null)
        {
            if (pr.getTimestamp().compareTo(c.getTimestamp()) > 0)
            {
                try
                {
                    failure("RM2");
                    throw new Exception("[RM2] ARREO Exception nel comparare " + pr.getTimestamp() + " > " + c.getTimestamp());
                } catch (Exception ex)
                {
                    Logger.getLogger(RM2QueueHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                pendingQueue.remove(pr);
            }
            else
            {
                pr.setTimestamp(c.getTimestamp());
                pr.setAsCommitted();
                Collections.sort(pendingQueue);

                tryDelivery();
            }
        }
        else
        {
            //FAULF!
            failure("RM2");
        }
    }

    @Lock(LockType.WRITE)
    private void tryDelivery()
    {
        PendingRequest p = pendingQueue.getFirst();
        
        while(p != null && p.isCommitted())
        {
            deliver(p);
            p = pendingQueue.isEmpty() ? null : pendingQueue.getFirst();
        }
    }
    
    @Lock(LockType.WRITE)
    private void deliver(PendingRequest p) 
    {
        PendingRequest deliveredRequest = pendingQueue.removeFirst();
        
        if(deliveredRequest.isOperation()) //scrittura
        {
            try 
            {
                Operation op = (Operation) deliveredRequest.getRequest();
                createEntry(op.getOperationID(), op.getClientID(), op.getOperationValue()); //scrivo su DB
                
                //replicaVersion = p.getTimestamp().getClone();
                
                replicaVersion = (RMTimestamp) p.getTimestamp().clone();
            } 
            catch (CloneNotSupportedException ex) {
                Logger.getLogger(RM1QueueHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else //lettura
        {
            List<OperationRecord> operationsList = getAllOperations();
            
            RMTimestamp replicaVersionForRequest;
            try
            {
                replicaVersionForRequest = (RMTimestamp) replicaVersion.clone();
                ReadReply rr = new ReadReply(p.getMessageID(), replicaVersionForRequest, operationsList); //leggo da DB
                sendJMSMessageToRMQueue(p.getRequest().getReplyTo(), rr);
            } 
            catch (CloneNotSupportedException ex)
            {
                Logger.getLogger(RM2QueueHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private void sendJMSMessageToRMQueue(Destination d, Serializable s)
    {
        context.createProducer().send(d, s);
    }
    
    private List<OperationRecord> getAllOperations()
    {
        //richiamo la findAll, gli oggetti di tipo conto funzionano
        List<conto.ws.Conto> operationsListPorted = findAll(); 
        
        List<OperationRecord> operationsList = new LinkedList<>();
        
        for(conto.ws.Conto o: operationsListPorted)
        {
            operationsList.add(new OperationRecord(o.getOperationID(), o.getUserID(), o.getOperationValue(), Long.parseLong(o.getOperationDate())));
        }
        return operationsList;
        
    }

    private void createEntry(java.lang.String operationID, java.lang.String userID, double operationValue)
    {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        conto.ws.Entity2WebService port = service.getEntity2WebServicePort();
        port.createEntry(operationID, userID, operationValue);
    }

    private java.util.List<conto.ws.Conto> findAll()
    {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        conto.ws.Entity2WebService port = service.getEntity2WebServicePort();
        return port.findAll();
    }

    private void keepAlive(java.lang.String processID)
    {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        fault.ws.FaultDetectorWS port = service_2.getFaultDetectorWSPort();
        port.keepAlive(processID);
    }

    private void failure(java.lang.String processID)
    {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        fault.ws.FaultDetectorWS port = service_2.getFaultDetectorWSPort();
        port.failure(processID);
    }

   
}
