package cz.iocb.idsm.debuggerbenchmark;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
			System.err.println("Usage: java -jar yourapp.jar <filePath> <url>");
			System.exit(1);
		}

		String filePath = args[0];
		String debugStr = args[1];

		benchmarkTest.processQueries(filePath, Boolean.valueOf(debugStr));

		System.exit(0);
	}
}
