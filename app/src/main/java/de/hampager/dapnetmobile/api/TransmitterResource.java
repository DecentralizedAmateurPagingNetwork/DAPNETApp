
package de.hampager.dapnetmobile.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TransmitterResource {
    //Currently not assigned: authKey, nodeName, address, deviceType, deviceVersion, callCount, antennaAbobeGroundLevel,antennaType,a ntennaDirection,
    private String name;
    private String authKey;
    private double longitude;
    private double latitude;
    private double power;
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

    public TransmitterResource(String name, double longitude, double latitude, double power, String timeSlot, List<String> ownerNames, String status, String usage) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.power = power;
        this.timeSlot = timeSlot;
        this.ownerNames = ownerNames;
        this.status = status;
        this.usage = usage;
    }

    public TransmitterResource(String name, String authKey, double longitude, double latitude, double power, String nodeName, Address address, String timeSlot, List<String> ownerNames, String deviceType, String deviceVersion, Integer callCount, String status, Integer antennaAboveGroundLevel, String antennaType, Integer antennaDirection, String antennaGainDbi, Object lastUpdate, String usage, Integer identificationAddress, String lastConnected, Object connectedSince) {
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

    public TransmitterResource(String name, String authKey, double longitude, double latitude, double power, String nodeName, String timeSlot, List<String> ownerNames, String deviceType, String deviceVersion, Integer callCount, String status, Integer antennaAboveGroundLevel, String antennaType, Integer antennaDirection, String antennaGainDbi, Object lastUpdate, String usage, Integer identificationAddress, String lastConnected, Object connectedSince) {
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


    public double getPower() {
        return power;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public List<String> getOwnerNames() {
        return ownerNames;
    }

    public String getStatus() {
        return status;
    }

    public String getUsage() {
        return usage;
    }
}
