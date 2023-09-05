export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
#export DEPLOYMENT_DIR=$GLASSFISH_HOME/glassfish/domains/domain1/autodeploy/
mvn clean package && docker build -t org.example/java-ee .
docker rm -f java-ee || true && docker run -p 8080:8080 -p 4848:4848 --name java-ee org.example/java-ee
