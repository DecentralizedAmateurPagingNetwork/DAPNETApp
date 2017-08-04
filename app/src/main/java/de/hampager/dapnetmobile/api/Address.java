
package de.hampager.dapnetmobile.api;

import java.util.HashMap;
import java.util.Map;

public class Address {

    private String ipAddr;
    private Integer port;
    private Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Address() {
    }

    /**
     * 
     * @param port IP Port
     * @param ipAddr IP Address
     */
    public Address(String ipAddr, Integer port) {
        super();
        this.ipAddr = ipAddr;
        this.port = port;
    }


}
