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
import conto.ws.Entity3WebService_Service;

import java.io.Serializable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import javax.ejb.Singleton;

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
public class RM3QueueHandler implements RM3QueueHandlerLocal
{

    @WebServiceRef(wsdlLocation = "META-INF/wsdl/localhost_8080/Entity3/Entity3WebService.wsdl")
    private Entity3WebService_Service service_1;

    @WebServiceRef(wsdlLocation = "META-INF/wsdl/localhost_8080/Entity3/Entity3WebService.wsdl")
    private Entity3WebService_Service service;

    @Resource(mappedName = "jms/RMQueue")
    private Queue rMQueue;

    @Inject
    @JMSConnectionFactory("java:comp/DefaultJMSConnectionFactory")
    private JMSContext context;
    
    private LinkedList<PendingRequest> pendingQueue;
    private RMTimestamp timestamp;
    private RMTimestamp replicaVersion;
    
    @PostConstruct
    private void initialize()
    {
        pendingQueue = new LinkedList<>();
        timestamp = new RMTimestamp(0, "RM1");
        replicaVersion = new RMTimestamp(0, "");
    }

    @Override
    public void enqueue(String messageID, Request request) 
    {
        timestamp.incrementCounter();
        PendingRequest pr = new PendingRequest(messageID, timestamp, request);
        pendingQueue.add(pr);
        Collections.sort(pendingQueue);
        
        //Se è scrittura mando la mia proposta
        if(request instanceof Operation)
        {
            Proposal myProposal = new Proposal(messageID, timestamp);
            sendJMSMessageToRMQueue(request.getReplyTo(), myProposal);
        }
        else if(request instanceof Read)
        {
            tryDelivery();
        }
    }
    
    private PendingRequest getRequestByMessaggeID(String messageID)
    {
        for(PendingRequest pr: pendingQueue)
            if (pr.getMessageID().equalsIgnoreCase(messageID))
                return pr;
        
        return null;
    }

    
    @Override
    public void abort(Abort a)
    {
        PendingRequest pr = getRequestByMessaggeID(a.getMessageID());
        
        if(pr != null)
        {
            pendingQueue.remove(pr);
        }
    }
    
    @Override
    public void commit(Commitment c)
    {
        PendingRequest pr = getRequestByMessaggeID(c.getMessageID());
        
        if(pr != null)
        {
            if (pr.getTimestamp().compareTo(c.getTimestamp()) > 0)
            {
                //TODO: FAULF!
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
            //TODO: FAULF!
        }
    }

    private void tryDelivery()
    {
        PendingRequest p = pendingQueue.getFirst();
        
        while(p != null && p.isCommitted())
        {
            deliver(p);
            p = pendingQueue.isEmpty() ? null : pendingQueue.getFirst();
        }
    }
    
    private void deliver(PendingRequest p)
    {
        PendingRequest deliveredRequest = pendingQueue.removeFirst();
        if(deliveredRequest.isOperation()) //scrittura
        {
            Operation op = (Operation) deliveredRequest.getRequest();
            createEntry(op.getOperationID(), op.getClientID(), op.getOperationValue()); //scrivo su DB
            replicaVersion = p.getTimestamp().getClone();
        }
        else //lettura
        {
            List<OperationRecord> operationsList = getAllOperations();
            
            ReadReply rr = new ReadReply(p.getMessageID(), replicaVersion, operationsList); //leggo da DB
            sendJMSMessageToRMQueue(p.getRequest().getReplyTo(), rr);
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

    private java.util.List<conto.ws.Conto> findAll()
    {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        conto.ws.Entity3WebService port = service_1.getEntity3WebServicePort();
        return port.findAll();
    }

    private void createEntry(java.lang.String operationID, java.lang.String userID, double operationValue)
    {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        conto.ws.Entity3WebService port = service_1.getEntity3WebServicePort();
        port.createEntry(operationID, userID, operationValue);
    }
    
    
    
    
    
    
    
    
    



    
    
    
}
