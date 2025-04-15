package cz.iocb.idsm.debuggerbenchmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.iocb.idsm.debuggerbenchmark.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BenchmarkTest {

    private final RestTemplate restTemplate;
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
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public void processQueries(String filePath, Boolean debug) {

        Map<String, Query> respMap = new HashMap<>();

        List<Query> queries = parseQueriesFromFile(filePath);
        for (Query query : queries) {
            try {
                long startTime = System.currentTimeMillis();

                if(debug) {
                    ResponseEntity<Tree<EndpointCall>> queryResp = executeDebugQuery(query);
                    Long queryId = queryResp.getBody().getRoot().getData().getQueryId();
                    query.setQueriId(queryId);
                } else {
                    executeQuery(query);
                }

                long endTime = System.currentTimeMillis();

                query.setDuration((endTime - startTime) / 1000.0);

                storeQueryToFile(query);
                respMap.put(query.getName(), query);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void storeQueryToFile(Query query) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultsFileStr, true))) {

        String line = query.getName() + "\t" + query.getDuration() + "\t" + query.getQueriId();
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

    private void executeQuery(Query query) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("accept", "application/sparql-results+json,*/*;q=0.9");

            MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
            formData.add("query", query.getQuery());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, headers);
            restTemplate.exchange(
                    query.getEndpoint(),
                    HttpMethod.POST,
                    requestEntity,
                    String.class);
        } catch (Exception e) {
            throw new RuntimeException("Query execution error.", e);
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
                "POST", new ArrayList<String>(), new ArrayList<String>(), new HashMap<>(),  "application/n-triples,*/*;q=0.9"
        );

        ObjectMapper objectMapper = new ObjectMapper();

        String requestContextJson = objectMapper.writeValueAsString(requestContext);

        return requestContextJson;
    }
}
