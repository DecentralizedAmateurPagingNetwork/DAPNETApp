
package de.hampager.dapnetmobile.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransmitterGroupResource implements Serializable {

    private String name;
    private String description;
    private List<String> transmitterNames = null;
    private List<String> ownerNames = null;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public TransmitterGroupResource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTransmitterNames() {
        return transmitterNames;
    }

    public void setTransmitterNames(List<String> transmitterNames) {
        this.transmitterNames = transmitterNames;
    }

    public List<String> getOwnerNames() {
        return ownerNames;
    }

    public void setOwnerNames(List<String> ownerNames) {
        this.ownerNames = ownerNames;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
