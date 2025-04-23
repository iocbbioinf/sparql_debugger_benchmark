package cz.iocb.idsm.debuggerbenchmark;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class BenchmarkConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // 1 hour timeout
        Duration timeout = Duration.ofSeconds(3600);

        // Build a Java11 HttpClient that always follows redirects
        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(timeout)
                .build();

        // Plug it into Spring's JdkClientHttpRequestFactory
        return builder
                .requestFactory(() -> new JdkClientHttpRequestFactory(httpClient))
                .build();
    }
}
