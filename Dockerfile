FROM glassfish:5.0
COPY ./target/java-ee-0.0.1-SNAPSHOT.war ${DEPLOYMENT_DIR}
