
package de.hampager.dapnetmobile.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallSignResource implements Serializable {
    private String name;
    private String description;
    private List<Pager> pagers = null;
    private List<String> ownerNames = null;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public CallSignResource(String name) {
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

    public List<Pager> getPagers() {
        return pagers;
    }

    public void setPagers(List<Pager> pagers) {
        this.pagers = pagers;
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
