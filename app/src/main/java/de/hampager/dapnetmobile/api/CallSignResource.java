
package de.hampager.dapnetmobile.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class CallSignResource implements Serializable {
    private String name;
    private String description;
    private ArrayList<Pager> pagers = null;
    private ArrayList<String> ownerNames = null;

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
        this.pagers = (ArrayList<Pager>) pagers;
    }

    public List<String> getOwnerNames() {
        return ownerNames;
    }

    public void setOwnerNames(List<String> ownerNames) {
        this.ownerNames = (ArrayList<String>) ownerNames;
    }

}
