
package de.hampager.dapnetmobile.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Pager implements Serializable {

    private Integer number;
    private String name;
    private Map<String, Object> additionalProperties = new HashMap<>();

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
