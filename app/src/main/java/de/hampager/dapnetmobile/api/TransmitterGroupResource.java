
package de.hampager.dapnetmobile.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TransmitterGroupResource implements Serializable {

    private String name;
    private String description;
    private ArrayList<String> transmitterNames = null;
    private ArrayList<String> ownerNames = null;

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
        this.transmitterNames = (ArrayList<String>) transmitterNames;
    }

    public List<String> getOwnerNames() {
        return ownerNames;
    }

    public void setOwnerNames(List<String> ownerNames) {
        this.ownerNames = (ArrayList<String>) ownerNames;
    }


}
