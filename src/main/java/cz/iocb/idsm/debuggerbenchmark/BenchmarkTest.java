package cz.iocb.idsm.debuggerbenchmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.iocb.idsm.debuggerbenchmark.model.EndpointCall;
import cz.iocb.idsm.debuggerbenchmark.model.Query;
import cz.iocb.idsm.debuggerbenchmark.model.RequestContext;
import cz.iocb.idsm.debuggerbenchmark.model.Tree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@Service
public class BenchmarkTest {

    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    @Value("${benchmark.queryDelimiter}")
    private String queryDelimiter;

    @Value("${benchmark.endpointDelimiter}")
    private String endpointDelimiter;

    @Value("${benchmark.debugger.url}")
    private String debuggerUrl;

    @Value("${benchmark.results.file}")
    private String resultsFileStr;


    public BenchmarkTest() {
        this.objectMapper = new ObjectMapper();
    }

    public void processQueries(String filePath, Boolean debug, Boolean storeResults) {

        Map<String, Query> respMap = new HashMap<>();

        List<Query> queries = parseQueriesFromFile(filePath);
        for (Query query : queries) {
            try {
                long startTime = System.currentTimeMillis();

                if(debug) {
                    ResponseEntity<Tree<EndpointCall>> queryResp = executeDebugQuery(query);

                    query.setQueryId(queryResp.getBody().getRoot().getData().getQueryId());
                    query.setRootCallId(queryResp.getBody().getRoot().getData().getNodeId());
                } else {
                    executeQuery(query, storeResults);
                }

                long endTime = System.currentTimeMillis();

                query.setDuration((endTime - startTime) / 1000.0);

                storeQueryInfoToFile(query);

                if(debug && storeResults) {
                    storeDebugResults(query);
                }

                respMap.put(query.getName(), query);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void storeDebugResults(Query query) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/sparql-results+json,*/*;q=0.9");

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("query", query.getQuery());

        RequestCallback requestCallback = request -> {
            request.getHeaders().addAll(headers);
        };

        ResponseExtractor<Void> responseExtractor = response -> {
            saveStreamToFile(response.getBody(), format("./results/%s.json", query.getName()));
            return null;
        };

        try {
            restTemplate.execute(
                    debuggerUrl + "/query/" + query.getQueryId() + "/call/" + query.getRootCallId() + "/response",
                    HttpMethod.GET,
                    requestCallback,
                    responseExtractor);
        } catch (Exception e) {
            throw new RuntimeException("Query execution error.", e);
        }


    }

    private void storeQueryInfoToFile(Query query) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultsFileStr, true))) {

        String line = query.getName() + "\t" + query.getDuration() + "\t" + query.getQueryId();
        writer.write(line);
        writer.newLine();
        } catch (Exception e) {
            throw new RuntimeException("Cannot store results to CSV file", e);
        }
    }

    private ResponseEntity<Tree<EndpointCall>> executeDebugQuery(Query query) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("accept", "application/json, text/plain, */*");

            MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
            formData.add("endpoint", query.getEndpoint());
            formData.add("query", query.getQuery());
            formData.add("requestcontext", createRequestContext());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, headers);
            return restTemplate.exchange(
                    debuggerUrl + "/syncquery",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Query execution error.", e);
        }
    }

    private void executeQuery(Query query, boolean storeResults) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Accept", "application/sparql-results+json,*/*;q=0.9");

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("query", query.getQuery());

        RequestCallback requestCallback = request -> {
            request.getHeaders().addAll(headers);
            new FormHttpMessageConverter().write(formData, MediaType.APPLICATION_FORM_URLENCODED, request);
        };

        ResponseExtractor<Void> responseExtractor = response -> {
            if(storeResults) {
                saveStreamToFile(response.getBody(), format("./results/%s.json", query.getName()));
            }
            return null;
        };

        try {
            restTemplate.execute(
                    query.getEndpoint(),
                    HttpMethod.POST,
                    requestCallback,
                    responseExtractor);
        } catch (Exception e) {
            throw new RuntimeException("Query execution error.", e);
        }
    }

    private void saveStreamToFile(InputStream inStream, String fileStr) {
            if (inStream == null) {
                throw new RuntimeException("Empty response body");
            }

            try (OutputStream fileOut = new FileOutputStream(fileStr)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inStream.read(buffer)) != -1) {
                    fileOut.write(buffer, 0, bytesRead);
                }
            } catch (Exception e) {
                throw new RuntimeException("Err: saving stream to file", e);
            }
    }

    private List<Query> parseQueriesFromFile(String filePath) {
        List<Query> queries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder queryBuilder = new StringBuilder();
            String line;
            do {
                String querName = reader.readLine();
                String endpoint = reader.readLine();
                if(!reader.readLine().equals(endpointDelimiter)) {
                    throw new RuntimeException("Wrong query file format");
                }

                line = reader.readLine();
                while(line != null && !line.equals(queryDelimiter)) {
                    if (!line.trim().equals(queryDelimiter)) {
                        queryBuilder.append(line).append("\n");
                    }
                    line = reader.readLine();
                }
                if (!queryBuilder.isEmpty()) {
                    queries.add(new Query(querName, endpoint, queryBuilder.toString().trim()));
                    queryBuilder.setLength(0);
                }
            } while(line != null);

        } catch (IOException e) {
            throw new RuntimeException("Unable to read query file", e);
        }
        return queries;
    }

    private String createRequestContext() throws JsonProcessingException {
        RequestContext requestContext = new RequestContext(
                "POST", "application/n-triples,*/*;q=0.9",
                "application/sparql-results+json,*/*;q=0.9",
                "text/plain,*/*;q=0.9",
                new ArrayList<String>(), new ArrayList<String>(), new HashMap<>()
        );

        ObjectMapper objectMapper = new ObjectMapper();

        String requestContextJson = objectMapper.writeValueAsString(requestContext);

        return requestContextJson;
    }
}
