# SPARQL debugger benchmark tool

**SPARQL Debugger Benchmark**  is a tool designed to evaluate the performance of a SPARQL Debugger server [here](https://github.com/iocbbioinf/sparql_debugger_server). It runs a predefined list of queries both with and without the debugger, measures the execution time for each, and stores the results. This allows for a direct comparison to verify whether the outputs produced via the debugger match those obtained directly from the SPARQL endpoints. 

## Configuration

All config. properties are stored in the file: `/src/main/java/resources/application.properties`

## Build, Package, Run

This is a Spring Boot Web application built using Gradle. You can use the following Gradle tasks (with Gradle wrapper `.gradlew`)

- `build`: Compiles the project.
- `bootJar`: Packages the application into a JAR file.
- `bootRun`: Runs the application. 

### Application Arguments

- queries file path 
- useDebugger - true/false 
- storeResults - true/false

### Run app

- by Gradle: `./gradlew bootRun --args './queries.txt true true'`
- or by Java: `java -jar path_to_jar/debuggerbenchmark-0.0.1-SNAPSHOT.jar ./queries.txt true true`
- Java version >= 21 required
