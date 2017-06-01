
package de.hampager.dapnetmobile.api;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatsResource {

    private Integer users;
    private Integer calls;
    private Integer callSigns;
    private Integer news;
    private Integer rubrics;
    private Integer nodesTotal;
    private Integer nodesOnline;
    private Integer transmittersTotal;
    private Integer transmittersOnline;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getUsers() {
        return users;
    }

    public void setUsers(Integer users) {
        this.users = users;
    }

    public Integer getCalls() {
        return calls;
    }

    public void setCalls(Integer calls) {
        this.calls = calls;
    }

    public Integer getCallSigns() {
        return callSigns;
    }

    public void setCallSigns(Integer callSigns) {
        this.callSigns = callSigns;
    }

    public Integer getNews() {
        return news;
    }

    public void setNews(Integer news) {
        this.news = news;
    }

    public Integer getRubrics() {
        return rubrics;
    }

    public void setRubrics(Integer rubrics) {
        this.rubrics = rubrics;
    }

    public Integer getNodesTotal() {
        return nodesTotal;
    }

    public void setNodesTotal(Integer nodesTotal) {
        this.nodesTotal = nodesTotal;
    }

    public Integer getNodesOnline() {
        return nodesOnline;
    }

    public void setNodesOnline(Integer nodesOnline) {
        this.nodesOnline = nodesOnline;
    }

    public Integer getTransmittersTotal() {
        return transmittersTotal;
    }

    public void setTransmittersTotal(Integer transmittersTotal) {
        this.transmittersTotal = transmittersTotal;
    }

    public Integer getTransmittersOnline() {
        return transmittersOnline;
    }

    public void setTransmittersOnline(Integer transmittersOnline) {
        this.transmittersOnline = transmittersOnline;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
    public LinkedHashMap<String,Integer> getStats(){
        LinkedHashMap<String,Integer> map=new LinkedHashMap<>();
        map.put("Users",users);
        map.put("Calls",calls);
        map.put("Callsigns",callSigns);
        map.put("News",news);
        map.put("Rubrics",rubrics);
        map.put("Nodes",nodesTotal);
        map.put("NodesUp",nodesOnline);
        map.put("Transmitters",transmittersTotal);
        map.put("TransmittersUp",transmittersOnline);
        return map;
    }
}
