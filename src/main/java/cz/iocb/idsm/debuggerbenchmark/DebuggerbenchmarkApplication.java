package cz.iocb.idsm.debuggerbenchmark;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;

@SpringBootApplication
public class DebuggerbenchmarkApplication implements CommandLineRunner {

	private final BenchmarkTest benchmarkTest;

	@Autowired
	public DebuggerbenchmarkApplication(BenchmarkTest benchmarkTest) {
		this.benchmarkTest = benchmarkTest;
	}

	public static void main(String[] args) {
		SpringApplication.run(DebuggerbenchmarkApplication.class, args);
	}

	@Override
	public void run(String... args) {
		if (args.length < 2) {
			System.err.println("Usage: java -jar debuggerbenchmark-0.0.1-SNAPSHOT.jar <filePath> <url>");
			System.exit(1);
		}

		String filePath = args[0];
		String debugStr = args[1];
		String storeResults = args[2];

		HttpURLConnection.setFollowRedirects(true);

		benchmarkTest.processQueries(filePath, Boolean.valueOf(debugStr), Boolean.valueOf(storeResults));

		System.exit(0);
	}
}
