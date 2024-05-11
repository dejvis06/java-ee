FROM glassfish
COPY ./target/java-ee-0.0.1-SNAPSHOT.war /usr/local/glassfish4/glassfish/domains/domain1/autodeploy
