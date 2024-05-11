# Build
mvn clean package && docker build -t com.example/java-ee .

# RUN

docker rm -f java-ee || true && docker run -d -p 8080:8080 -p 4848:4848 --name java-ee com.example/java-ee 