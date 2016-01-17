
package conto.rm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per operationRecord complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="operationRecord"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="currentDate" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="operationID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="operationValue" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="userID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "operationRecord", propOrder = {
    "currentDate",
    "operationID",
    "operationValue",
    "userID"
})
public class OperationRecord {

    protected long currentDate;
    protected String operationID;
    protected double operationValue;
    protected String userID;

    /**
     * Recupera il valore della proprietà currentDate.
     * 
     */
    public long getCurrentDate() {
        return currentDate;
    }

    /**
     * Imposta il valore della proprietà currentDate.
     * 
     */
    public void setCurrentDate(long value) {
        this.currentDate = value;
    }

    /**
     * Recupera il valore della proprietà operationID.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperationID() {
        return operationID;
    }

    /**
     * Imposta il valore della proprietà operationID.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperationID(String value) {
        this.operationID = value;
    }

    /**
     * Recupera il valore della proprietà operationValue.
     * 
     */
    public double getOperationValue() {
        return operationValue;
    }

    /**
     * Imposta il valore della proprietà operationValue.
     * 
     */
    public void setOperationValue(double value) {
        this.operationValue = value;
    }

    /**
     * Recupera il valore della proprietà userID.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Imposta il valore della proprietà userID.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserID(String value) {
        this.userID = value;
    }

}
