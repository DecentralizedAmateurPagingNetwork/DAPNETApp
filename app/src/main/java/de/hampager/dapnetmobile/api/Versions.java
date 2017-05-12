package de.hampager.dapnetmobile.api;

public class Versions {

    private String core;
    private String api;

    /**
     * No args constructor for use in serialization
     */
    public Versions() {
    }

    /**
     * @param api
     * @param core
     */
    public Versions(String core, String api) {
        super();
        this.core = core;
        this.api = api;
    }

    public String getCore() {
        return core;
    }

    public void setCore(String core) {
        this.core = core;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

}
