package cz.iocb.idsm.debuggerbenchmark.model;

import java.util.List;
import java.util.Map;

public class RequestContext {

    private String endpoint;
    private String method;
    private String acceptHeaderGraph;
    private String acceptHeaderSelect;
    private String acceptHeaderUpdate;
    private List<String> namedGraphs;
    private List<String> defaultGraphs;
    private List<RequestArgument> args;
    private Map<String, String> headers;
    private boolean withCredentials;
    private boolean adjustQueryBeforeRequest;

    public RequestContext() {
    }


    public RequestContext(String method, String acceptHeaderGraph, String acceptHeaderSelect, String acceptHeaderUpdate, List<String> namedGraphs, List<String> defaultGraphs, Map<String, String> headers) {
        this.method = method;
        this.acceptHeaderGraph = acceptHeaderGraph;
        this.acceptHeaderSelect = acceptHeaderSelect;
        this.acceptHeaderUpdate = acceptHeaderUpdate;
        this.namedGraphs = namedGraphs;
        this.defaultGraphs = defaultGraphs;
        this.headers = headers;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getAcceptHeaderGraph() {
        return acceptHeaderGraph;
    }

    public void setAcceptHeaderGraph(String acceptHeaderGraph) {
        this.acceptHeaderGraph = acceptHeaderGraph;
    }

    public String getAcceptHeaderSelect() {
        return acceptHeaderSelect;
    }

    public void setAcceptHeaderSelect(String acceptHeaderSelect) {
        this.acceptHeaderSelect = acceptHeaderSelect;
    }

    public String getAcceptHeaderUpdate() {
        return acceptHeaderUpdate;
    }

    public void setAcceptHeaderUpdate(String acceptHeaderUpdate) {
        this.acceptHeaderUpdate = acceptHeaderUpdate;
    }

    public List<String> getNamedGraphs() {
        return namedGraphs;
    }

    public void setNamedGraphs(List<String> namedGraphs) {
        this.namedGraphs = namedGraphs;
    }

    public List<String> getDefaultGraphs() {
        return defaultGraphs;
    }

    public void setDefaultGraphs(List<String> defaultGraphs) {
        this.defaultGraphs = defaultGraphs;
    }

    public List<RequestArgument> getArgs() {
        return args;
    }

    public void setArgs(List<RequestArgument> args) {
        this.args = args;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public boolean isWithCredentials() {
        return withCredentials;
    }

    public void setWithCredentials(boolean withCredentials) {
        this.withCredentials = withCredentials;
    }

    public boolean isAdjustQueryBeforeRequest() {
        return adjustQueryBeforeRequest;
    }

    public void setAdjustQueryBeforeRequest(boolean adjustQueryBeforeRequest) {
        this.adjustQueryBeforeRequest = adjustQueryBeforeRequest;
    }
}
