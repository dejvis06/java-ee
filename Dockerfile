FROM payara/micro:5.2021.1
COPY ./target/java-ee.war $DEPLOY_DIR
