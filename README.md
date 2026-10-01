## Running Tests

The project contains both unit tests and Quarkus integration tests.

### Unit Tests

Unit tests use JUnit and Mockito to test individual classes in isolation.

They do not require Quarkus, PostgreSQL, Kafka, Elasticsearch or Docker.

Run all tests with:

```bash
./mvnw test
```

Run a specific test with:

```bash
./mvnw test -Dtest=BaseServiceTest
```

### Quarkus Tests

Tests annotated with `@QuarkusTest` start the Quarkus application in test mode and can test multiple layers together:

```text
REST
 ↓
Service
 ↓
Repository / Hibernate ORM
 ↓
PostgreSQL
```

REST Assured is used to call the REST API and verify responses.

When no test database URL is configured, Quarkus Dev Services uses Testcontainers to automatically start a temporary PostgreSQL database.

```text
./mvnw test
       ↓
Quarkus Test
       ↓
Dev Services
       ↓
Testcontainers
       ↓
Temporary PostgreSQL
```

The development database is configured separately:

```properties
%dev.quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/vietnam_logistics
```

This prevents tests from modifying development data.

Docker must be available from WSL:

```bash
docker ps
```

---

## JVM vs GraalVM Native

Quarkus applications can run as traditional JVM applications or be compiled ahead of time into native executables using GraalVM/Mandrel.

This project can be used to compare both approaches.

### JVM Build

Build the JVM application:

```bash
time ./mvnw clean package -DskipTests
```

The application is generated under:

```text
target/quarkus-app/
```

Run it with:

```bash
export QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://localhost:5432/vietnam_logistics
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092
export QUARKUS_ELASTICSEARCH_HOSTS=localhost:9200

java -jar target/quarkus-app/quarkus-run.jar
```

Measure the complete JVM application size with:

```bash
du -sh target/quarkus-app
```

### Native Build

A native executable can be built using the Quarkus Mandrel builder container.

This avoids requiring GraalVM to be installed locally:

```bash
time ./mvnw clean package \
  -Dnative \
  -DskipTests \
  -Dquarkus.native.container-build=true
```

Native compilation performs ahead-of-time analysis and compilation and therefore takes considerably longer than a JVM build.

The resulting executable is:

```text
target/quarkus-vietnam-logistics-1.0.0-SNAPSHOT-runner
```

Run it using the same configuration:

```bash
export QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://localhost:5432/vietnam_logistics
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092
export QUARKUS_ELASTICSEARCH_HOSTS=localhost:9200

./target/quarkus-vietnam-logistics-1.0.0-SNAPSHOT-runner
```

### Measuring Memory

Find the process:

```bash
pgrep -af quarkus-vietnam-logistics
```

Then inspect its resident memory:

```bash
ps -o pid,rss,vsz,etime,cmd -p <PID>
```

`RSS` represents the physical memory currently resident in RAM and is the value used for this comparison.

### Results

Measurements were made using the same application and external PostgreSQL, Kafka and Elasticsearch services.

| Metric | JVM | GraalVM Native |
|---|---:|---:|
| Startup time | 8.566 s | 1.302 s |
| RSS memory | ~281.7 MiB | ~92.7 MiB |
| Native executable size | N/A | 131 MB |

In this test, the native application:

- started approximately **6.6× faster**
- used approximately **67% less resident memory**
- required a significantly longer build process

The native build moves work from runtime to build time:

```text
JVM
Java bytecode
     ↓
JVM startup + runtime/JIT compilation
     ↓
Application


Native
Application + dependencies
     ↓
GraalVM reachability analysis
     ↓
Ahead-of-time compilation
     ↓
Native executable
     ↓
Application
```

This illustrates the main trade-off of native compilation: **slower and more resource-intensive builds in exchange for faster startup and lower runtime memory usage**.