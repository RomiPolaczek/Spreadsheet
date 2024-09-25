//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.5 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
//


package xmlGenerated;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element ref="{}STL-Boundaries"/>
 *       </sequence>
 *       <attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "stlBoundaries"
})
@XmlRootElement(name = "STL-Range")
public class STLRange {

    @XmlElement(name = "STL-Boundaries", required = true)
    protected STLBoundaries stlBoundaries;
    @XmlAttribute(name = "name", required = true)
    protected String name;

    /**
     * Gets the value of the stlBoundaries property.
     * 
     * @return
     *     possible object is
     *     {@link STLBoundaries }
     *     
     */
    public STLBoundaries getSTLBoundaries() {
        return stlBoundaries;
    }

    /**
     * Sets the value of the stlBoundaries property.
     * 
     * @param value
     *     allowed object is
     *     {@link STLBoundaries }
     *     
     */
    public void setSTLBoundaries(STLBoundaries value) {
        this.stlBoundaries = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
