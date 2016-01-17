
package conto.rm;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the conto.rm package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Conto_QNAME = new QName("http://ws.conto/", "conto");
    private final static QName _Count_QNAME = new QName("http://ws.conto/", "count");
    private final static QName _CountResponse_QNAME = new QName("http://ws.conto/", "countResponse");
    private final static QName _Create_QNAME = new QName("http://ws.conto/", "create");
    private final static QName _CreateEntry_QNAME = new QName("http://ws.conto/", "createEntry");
    private final static QName _Edit_QNAME = new QName("http://ws.conto/", "edit");
    private final static QName _Find_QNAME = new QName("http://ws.conto/", "find");
    private final static QName _FindAll_QNAME = new QName("http://ws.conto/", "findAll");
    private final static QName _FindAllResponse_QNAME = new QName("http://ws.conto/", "findAllResponse");
    private final static QName _FindRange_QNAME = new QName("http://ws.conto/", "findRange");
    private final static QName _FindRangeResponse_QNAME = new QName("http://ws.conto/", "findRangeResponse");
    private final static QName _FindResponse_QNAME = new QName("http://ws.conto/", "findResponse");
    private final static QName _GetAllOperations_QNAME = new QName("http://ws.conto/", "getAllOperations");
    private final static QName _GetAllOperationsResponse_QNAME = new QName("http://ws.conto/", "getAllOperationsResponse");
    private final static QName _Remove_QNAME = new QName("http://ws.conto/", "remove");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: conto.rm
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Conto }
     * 
     */
    public Conto createConto() {
        return new Conto();
    }

    /**
     * Create an instance of {@link Count }
     * 
     */
    public Count createCount() {
        return new Count();
    }

    /**
     * Create an instance of {@link CountResponse }
     * 
     */
    public CountResponse createCountResponse() {
        return new CountResponse();
    }

    /**
     * Create an instance of {@link Create }
     * 
     */
    public Create createCreate() {
        return new Create();
    }

    /**
     * Create an instance of {@link CreateEntry }
     * 
     */
    public CreateEntry createCreateEntry() {
        return new CreateEntry();
    }

    /**
     * Create an instance of {@link Edit }
     * 
     */
    public Edit createEdit() {
        return new Edit();
    }

    /**
     * Create an instance of {@link Find }
     * 
     */
    public Find createFind() {
        return new Find();
    }

    /**
     * Create an instance of {@link FindAll }
     * 
     */
    public FindAll createFindAll() {
        return new FindAll();
    }

    /**
     * Create an instance of {@link FindAllResponse }
     * 
     */
    public FindAllResponse createFindAllResponse() {
        return new FindAllResponse();
    }

    /**
     * Create an instance of {@link FindRange }
     * 
     */
    public FindRange createFindRange() {
        return new FindRange();
    }

    /**
     * Create an instance of {@link FindRangeResponse }
     * 
     */
    public FindRangeResponse createFindRangeResponse() {
        return new FindRangeResponse();
    }

    /**
     * Create an instance of {@link FindResponse }
     * 
     */
    public FindResponse createFindResponse() {
        return new FindResponse();
    }

    /**
     * Create an instance of {@link GetAllOperations }
     * 
     */
    public GetAllOperations createGetAllOperations() {
        return new GetAllOperations();
    }

    /**
     * Create an instance of {@link GetAllOperationsResponse }
     * 
     */
    public GetAllOperationsResponse createGetAllOperationsResponse() {
        return new GetAllOperationsResponse();
    }

    /**
     * Create an instance of {@link Remove }
     * 
     */
    public Remove createRemove() {
        return new Remove();
    }

    /**
     * Create an instance of {@link OperationRecord }
     * 
     */
    public OperationRecord createOperationRecord() {
        return new OperationRecord();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Conto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.conto/", name = "conto")
    public JAXBElement<Conto> createConto(Conto value) {
        return new JAXBElement<Conto>(_Conto_QNAME, Conto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Count }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.conto/", name = "count")
    public JAXBElement<Count> createCount(Count value) {
        return new JAXBElement<Count>(_Count_QNAME, Count.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.conto/", name = "countResponse")
    public JAXBElement<CountResponse> createCountResponse(CountResponse value) {
        return new JAXBElement<CountResponse>(_CountResponse_QNAME, CountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Create }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.conto/", name = "create")
    public JAXBElement<Create> createCreate(Create value) {
        return new JAXBElement<Create>(_Create_QNAME, Create.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateEntry }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.conto/", name = "createEntry")
    public JAXBElement<CreateEntry> createCreateEntry(CreateEntry value) {
        return new JAXBElement<CreateEntry>(_CreateEntry_QNAME, CreateEntry.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Edit }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.conto/", name = "edit")
    public JAXBElement<Edit> createEdit(Edit value) {
        return new JAXBElement<Edit>(_Edit_QNAME, Edit.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Find }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.conto/", name = "find")
    public JAXBElement<Find> createFind(Find value) {
        return new JAXBElement<Find>(_Find_QNAME, Find.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAll }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.conto/", name = "findAll")
    public JAXBElement<FindAll> createFindAll(FindAll value) {
        return new JAXBElement<FindAll>(_FindAll_QNAME, FindAll.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAllResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.conto/", name = "findAllResponse")
    public JAXBElement<FindAllResponse> createFindAllResponse(FindAllResponse value) {
        return new JAXBElement<FindAllResponse>(_FindAllResponse_QNAME, FindAllResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindRange }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.conto/", name = "findRange")
    public JAXBElement<FindRange> createFindRange(FindRange value) {
        return new JAXBElement<FindRange>(_FindRange_QNAME, FindRange.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindRangeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.conto/", name = "findRangeResponse")
    public JAXBElement<FindRangeResponse> createFindRangeResponse(FindRangeResponse value) {
        return new JAXBElement<FindRangeResponse>(_FindRangeResponse_QNAME, FindRangeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.conto/", name = "findResponse")
    public JAXBElement<FindResponse> createFindResponse(FindResponse value) {
        return new JAXBElement<FindResponse>(_FindResponse_QNAME, FindResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllOperations }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.conto/", name = "getAllOperations")
    public JAXBElement<GetAllOperations> createGetAllOperations(GetAllOperations value) {
        return new JAXBElement<GetAllOperations>(_GetAllOperations_QNAME, GetAllOperations.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllOperationsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.conto/", name = "getAllOperationsResponse")
    public JAXBElement<GetAllOperationsResponse> createGetAllOperationsResponse(GetAllOperationsResponse value) {
        return new JAXBElement<GetAllOperationsResponse>(_GetAllOperationsResponse_QNAME, GetAllOperationsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Remove }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.conto/", name = "remove")
    public JAXBElement<Remove> createRemove(Remove value) {
        return new JAXBElement<Remove>(_Remove_QNAME, Remove.class, null, value);
    }

}
