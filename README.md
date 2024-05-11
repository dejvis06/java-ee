# Java EE 8

## Docker, Build & Run

Execute buildAndRun.sh, it has the following content:
```shell
mvn clean install && docker build -t com.example/java-ee .
docker run -p 8080:8080 -p 4848:4848 --name java-ee com.example/java-ee
```
and it goes along with a Dockerfile:
```docker
FROM glassfish
COPY ./target/java-ee-0.0.1-SNAPSHOT.war /usr/local/glassfish4/glassfish/domains/domain1/autodeploy
```

After running you can click on this link [Main page](http://localhost:8080/java-ee-0.0.1-SNAPSHOT/) (check out the jsf files under webapp)

## GlassFish Server

To use Java EE specifications the following dependency must be included:
```xml
<dependency>
    <groupId>javax</groupId>
    <artifactId>javaee-api</artifactId>
    <version>8.0</version>
    <scope>provided</scope>
</dependency>
```
In this case the scope is: `<scope>provided</scope>` because the Glassfish runtime environment contains all the necessary java-ee specifications and its implementations:
- **Servlets**: For handling HTTP requests and responses.
- **JavaServer Faces (JSF)**: For building component-based web interfaces.
- **Enterprise JavaBeans (EJB)**: For developing scalable, transactional business components.
- **Java Persistence API (JPA)**: For object-relational mapping and data persistence.
- **Contexts and Dependency Injection (CDI)**: For dependency injection and lifecycle management.
- **Java API for RESTful Web Services (JAX-RS)**: For building RESTful web services.
- **Java Message Service (JMS)**: For messaging between components.
- **Java Transaction API (JTA)**: For managing transactions.
- **Java API for WebSocket**: For WebSocket communication.
- **Bean Validation**: For validating JavaBeans.

## CDI

### Understanding `beans.xml` Configuration

The `beans.xml` file is a key configuration file in a Java EE application that specifies how CDI (Contexts and Dependency Injection) beans are discovered and managed:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://xmlns.jcp.org/xml/ns/javaee"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/beans_2_0.xsd"
       bean-discovery-mode="all" version="2.0">
</beans>
```

#### `bean-discovery-mode`

Possible values:
- **all**: scans and registers all beans
- **annotated**: scans for and registers beans with CDI annotations
- **none**: no scan and registration

**Note**: The default value is `annotated`, so if you don’t include this `beans.xml` file, the annotated bean discovery mode will be active.

### Qualifiers

`@Qualifier` is a CDI annotation that is used to create custom qualifiers to distinguish between different implementations of a bean type.
Check out the `com.example.cdi.beans.salute.qualifiers` package, example:

```java
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
public @interface Soldier {
}
```

This is a `@Qualifier` type, used by the CDI during runtime: `@Retention(RetentionPolicy.RUNTIME)`
to scan the container and inject any `@Soldier` bean of the type Salute.java:
```java
@com.example.cdi.beans.salute.qualifiers.Soldier
public class Soldier implements Salute {
    @Override
    public String salute(String name) {
        return MessageFormat.format("Aye Aye Capt {0}", name);
    }
}

// QualifierBean.java
@Inject
@Soldier
private Salute soldierSalute;
```

A more advanced or maybe even cleaner way to configure qualifiers is to create a qualifier annotation and distinguish the types through enums:
```java
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
public @interface ServiceMan {

    ServiceType value();

    public enum ServiceType{
        SOLDIER, POLICE
    }
}

@ServiceMan(value = ServiceMan.ServiceType.SOLDIER)
public class Soldier implements Salute {
          ...
}

// QualifierWithValueBean.java
@Inject
@ServiceMan(value = ServiceMan.ServiceType.SOLDIER)
private Salute soldierSalute;
```

After running the app in docker you can click these link to check out the runtime implementations:
- [qualifier.xhtml](http://localhost:8080/java-ee-0.0.1-SNAPSHOT/qualifier.xhtml)
- [qualifier-with-value.xhtml](http://localhost:8080/java-ee-0.0.1-SNAPSHOT/qualifier-with-value.xhtml)
 