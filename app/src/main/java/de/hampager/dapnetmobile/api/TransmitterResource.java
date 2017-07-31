
package de.hampager.dapnetmobile.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransmitterResource {

    private String name;
    private String authKey;
    private double longitude;
    private double latitude;
    private String power;
    private String nodeName;
    private Address address;
    private String timeSlot;
    private List<String> ownerNames = null;
    private String deviceType;
    private String deviceVersion;
    private Integer callCount;
    private String status;
    private Integer antennaAboveGroundLevel;
    private String antennaType;
    private Integer antennaDirection;
    private String antennaGainDbi;
    private Object lastUpdate;
    private String usage;
    private Integer identificationAddress;
    private String lastConnected;
    private Object connectedSince;
    private Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public TransmitterResource() {
    }

    /**
     * 
     * @param identificationAddress
     * @param antennaType
     * @param antennaAboveGroundLevel
     * @param authKey
     * @param status
     * @param lastUpdate
     * @param nodeName
     * @param ownerNames
     * @param timeSlot
     * @param connectedSince
     * @param antennaDirection
     * @param lastConnected
     * @param address
     * @param deviceType
     * @param name
     * @param usage
     * @param antennaGainDbi
     * @param power
     * @param deviceVersion
     * @param longitude
     * @param latitude
     * @param callCount
     */
    public TransmitterResource(String name, String authKey, double longitude, double latitude, String power, String nodeName, Address address, String timeSlot, List<String> ownerNames, String deviceType, String deviceVersion, Integer callCount, String status, Integer antennaAboveGroundLevel, String antennaType, Integer antennaDirection, String antennaGainDbi, Object lastUpdate, String usage, Integer identificationAddress, String lastConnected, Object connectedSince) {
        super();
        this.name = name;
        this.authKey = authKey;
        this.longitude = longitude;
        this.latitude = latitude;
        this.power = power;
        this.nodeName = nodeName;
        this.address = address;
        this.timeSlot = timeSlot;
        this.ownerNames = ownerNames;
        this.deviceType = deviceType;
        this.deviceVersion = deviceVersion;
        this.callCount = callCount;
        this.status = status;
        this.antennaAboveGroundLevel = antennaAboveGroundLevel;
        this.antennaType = antennaType;
        this.antennaDirection = antennaDirection;
        this.antennaGainDbi = antennaGainDbi;
        this.lastUpdate = lastUpdate;
        this.usage = usage;
        this.identificationAddress = identificationAddress;
        this.lastConnected = lastConnected;
        this.connectedSince = connectedSince;
    }
    public TransmitterResource(String name, String authKey, double longitude, double latitude, String power, String nodeName, String timeSlot, List<String> ownerNames, String deviceType, String deviceVersion, Integer callCount, String status, Integer antennaAboveGroundLevel, String antennaType, Integer antennaDirection, String antennaGainDbi, Object lastUpdate, String usage, Integer identificationAddress, String lastConnected, Object connectedSince) {
        super();
        this.name = name;
        this.authKey = authKey;
        this.longitude = longitude;
        this.latitude = latitude;
        this.power = power;
        this.nodeName = nodeName;
        this.timeSlot = timeSlot;
        this.ownerNames = ownerNames;
        this.deviceType = deviceType;
        this.deviceVersion = deviceVersion;
        this.callCount = callCount;
        this.status = status;
        this.antennaAboveGroundLevel = antennaAboveGroundLevel;
        this.antennaType = antennaType;
        this.antennaDirection = antennaDirection;
        this.antennaGainDbi = antennaGainDbi;
        this.lastUpdate = lastUpdate;
        this.usage = usage;
        this.identificationAddress = identificationAddress;
        this.lastConnected = lastConnected;
        this.connectedSince = connectedSince;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }


    public double getLatitude() {
        return latitude;
    }


    private String getPower() {
        return power;
    }

    private String getTimeSlot() {
        return timeSlot;
    }

    private List<String> getOwnerNames() {
        return ownerNames;
    }

    public String getStatus() {
        return status;
    }

    public String getUsage() {
        return usage;
    }
    public String toString(){
        String s = "";
        s+="Usage: ";
        s+=getUsage();
        s+="\n";
        s+="Transmission Power (W): ";
        s+=getPower();
        s+="\n";
        s+="Timeslot: ";
        s+=getTimeSlot();
        s+="\n";
        s+="Owner: ";
        s+=getOwnerNames();
        return s;
    }
}
