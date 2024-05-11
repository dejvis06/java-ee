FROM airhacks/glassfish
COPY ./target/java-ee.war ${DEPLOYMENT_DIR}
