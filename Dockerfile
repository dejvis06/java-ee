FROM payara/micro:5.2021.1
COPY ./target/java-ee-0.0.1-SNAPSHOT.war /opt/payara/deployments/
