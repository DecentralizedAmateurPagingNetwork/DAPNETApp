package de.hampager.dapnetmobile.api;

import java.util.List;

public class HamnetCall {
    private String text;
    private List<String> callSignNames = null;
    private List<String> transmitterGroupNames = null;
    private Boolean emergency;
    private String timestamp;
    private String ownerName;

    public HamnetCall(String text, List<String> callSignNames, List<String> transmitterGroupNames, Boolean emergency) {
        this.text = text;
        this.callSignNames = callSignNames;
        this.transmitterGroupNames = transmitterGroupNames;
        this.emergency = emergency;
    }

    public HamnetCall(String text, List<String> callSignNames, List<String> transmitterGroupNames, Boolean emergency, String timestamp, String ownerName) {
        this.text = text;
        this.callSignNames = callSignNames;
        this.transmitterGroupNames = transmitterGroupNames;
        this.emergency = emergency;
        this.timestamp = timestamp;
        this.ownerName = ownerName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getCallSignNames() {
        return callSignNames;
    }

    public void setCallSignNames(List<String> callSignNames) {
        this.callSignNames = callSignNames;
    }

    public void setTransmitterGroupNames(List<String> transmitterGroupNames) {
        this.transmitterGroupNames = transmitterGroupNames;
    }

    public Boolean getEmergency() {
        return emergency;
    }

    public void setEmergency(Boolean emergency) {
        this.emergency = emergency;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public List<String> getTransmitterGroupNames() {
        return transmitterGroupNames;
    }

    @Override
    public String toString() {
        return "HamnetCall{" +
                "text='" + text + '\'' +
                ", callSignNames=" + callSignNames +
                ", transmitterGroupNames=" + transmitterGroupNames +
                ", emergency=" + emergency +
                ", timestamp='" + timestamp + '\'' +
                ", ownerName='" + ownerName + '\'' +
                '}';
    }
}
