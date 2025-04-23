package cz.iocb.idsm.debuggerbenchmark.model;

public class Query {

    String name;
    String endpoint;
    String query;
    Double duration;
    Long queryId;
    Long rootCallId;

    public Query(String name, String endpoint, String query) {
        this.name = name;
        this.endpoint = endpoint;
        this.query = query;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Long getQueryId() {
        return queryId;
    }

    public void setQueryId(Long queryId) {
        this.queryId = queryId;
    }

    public Long getRootCallId() {
        return rootCallId;
    }

    public void setRootCallId(Long rootCallId) {
        this.rootCallId = rootCallId;
    }
}
