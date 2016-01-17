
package conto.rm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per remove complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="remove"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="conto" type="{http://ws.conto/}conto" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "remove", propOrder = {
    "conto"
})
public class Remove {

    protected Conto conto;

    /**
     * Recupera il valore della proprietà conto.
     * 
     * @return
     *     possible object is
     *     {@link Conto }
     *     
     */
    public Conto getConto() {
        return conto;
    }

    /**
     * Imposta il valore della proprietà conto.
     * 
     * @param value
     *     allowed object is
     *     {@link Conto }
     *     
     */
    public void setConto(Conto value) {
        this.conto = value;
    }

}
