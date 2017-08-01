
package de.hampager.dapnetmobile.api;

import java.util.LinkedHashMap;
import java.util.Map;

public class StatsResource {

    private Integer users;
    private Integer calls;
    private Integer callsTotal;
    private Integer callSigns;
    private Integer news;
    private Integer newsTotal;
    private Integer rubrics;
    private Integer nodesTotal;
    private Integer nodesOnline;
    private Integer transmittersTotal;
    private Integer transmittersOnline;
    public Integer getCalls() {
        return calls;
    }

    public void setCalls(Integer calls) {
        this.calls = calls;
    }


    public Map<String, Integer> getStats() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put("Users", users);
        map.put("Calls", calls);
        map.put("CallsTotal",callsTotal);
        map.put("Callsigns", callSigns);
        map.put("News", news);
        map.put("NewsTotal",newsTotal);
        map.put("Rubrics", rubrics);
        map.put("Nodes", nodesTotal);
        map.put("NodesUp", nodesOnline);
        map.put("Transmitters", transmittersTotal);
        map.put("TransmittersUp", transmittersOnline);
        return map;
    }
}
