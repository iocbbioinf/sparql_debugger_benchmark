package cz.iocb.idsm.debuggerbenchmark.model;

public class Query {

    String name;
    String endpoint;
    String query;
    Double duration;
    Long queriId;

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

    public Long getQueriId() {
        return queriId;
    }

    public void setQueriId(Long queriId) {
        this.queriId = queriId;
    }
}
