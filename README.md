# Java EE 8

- [Docker, Build & Run](#docker-build--run)
- [GlassFish Server](#glassfish-server)
- [Payara Server](#payara-server)
- [CDI](#cdi)
    - [Understanding `beans.xml` Configuration](#understanding-beansxml-configuration)
        - [`bean-discovery-mode`](#bean-discovery-mode)
    - [Qualifiers](#qualifiers)
    - [Stereotypes](#stereotypes)
    - [Scopes](#scopes)
    - [Producers](#producers)
    - [Interceptors](#interceptors)
    - [Events](#events)
      - [Plain Events](#plain-events-no-qualifiers)
      - [Qualifier Events](#qualifier-events)
      - [Priority Events](#priority-events)
      - [Async Events](#async-events)
- [JPA](#jpa)
    - [`persistence.xml`](#persistencexml)
    - [Connections Pool](#connections-pool)
        - [File Configurations](#file-configurations)
        - [Script Init](#script-init)


## Docker, Build & Run
After running you can click on this link [Main page](http://localhost:8080/java-ee-0.0.1-SNAPSHOT/) (check out the jsf files under webapp)

### Application server container

Execute buildAndRun.sh, it has the following content:
```shell
mvn clean install && docker build -t com.example/java-ee .
docker run -p 8080:8080 -p 4848:4848 --name java-ee com.example/java-ee
```
and it goes along with the Dockerfile:
```docker
FROM glassfish
COPY ./target/java-ee-0.0.1-SNAPSHOT.war /usr/local/glassfish4/glassfish/domains/domain1/autodeploy
```

### With Postgresql container
Check out the docker-compose.yml, do a mvn clean package when necessary and use docker-compose up.

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

## Payara Server
Just like the glassfish server, payara offers support for the java ee specifications, but some or most of them may be updated versions. <br>
Specifically, the reason why the application server has been changed is because of the need for the CDI 2.0 specification support used in the [Events](#events) examples. <br>
In the beginning the _payara-micro_ version was used,
but later it was replaced with the _payara/server-full:5.2021.1_ because of the need to manage the JNDI-db-configuration through the _domain.xml_ file: [JPA](#jpa).

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
 
### Stereotypes

Custom annotation used in Qualifier beans:
```java
@Stereotype
@RequestScoped
@Named
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Web {
}
```
- **`@Stereotype`**: used for semantics, may be useful when its taken under consideration by other tools, such as documentation tools etc..
- **`@RequestScoped`**: bean lifecycle
- **`@Named`**: bean type registered in the CDI for JSF usage

### Scopes

- **`@RequestScoped`**: lifecycle tied to a single HTTP request
- **`@SessionScoped`**: lifecycle tied to http session
- **`@ApplicationScoped`**: lifecycle tied to the application lifecycle
- **`@Dependent`**: lifecycle depends on the bean it is injected into

Run the app, click on this link:  [scopes.xhtml](http://localhost:8080/java-ee-0.0.1-SNAPSHOT/scopes.xhtml) and check the hashcode generated for each bean scope type.

### Producers

Producing types during CDI scanning and injection: (producing through method example)
```java
@Singleton
public class LoggerProducer {

    @Produces
    // Defaults to dependent
    public Logger produceLogger(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }
}

// ScopesBean.java
@Inject
private Logger logger;
```

There can also be production through fields, also disposing of produced types:
```java

// produce through field
@Produces
        ...
         ...
@PersistenceContext
private EntityManager em;

// dispose
public void close(@Disposes @UserDatabase EntityManager em) {
    em.close();
}
```

The disposing happens based on the lifecycle of the bean where it is injected.

### Interceptors
Interceptors are components that allow you to intercept and add behavior to method calls or lifecycle events.

First a `@InterceptorBinding` annotation type is created:
```java
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface Logged {
}
```

Then an interceptor binding to the above mentioned annotation:
```java
@Logged
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class LoggedInterceptor {

    @Inject
    private Logger logger;

    @AroundInvoke
    public Object logMethodCall(InvocationContext context) throws Exception {
        try {
            return context.proceed(); // Proceed with method invocation
        } finally {
            // log after proceeding with method invocation
            logger.log(Level.INFO, "Intercepting method {0}", context.getMethod().getName());
        }
    }
}
```
The `@AroundInvoke` annotation is used by the CDI to handle the interception and continue with the method invocation.
In this example we first proceed with the method invocation and then do the custom interception logic (_before invocation_), but it can be handled in the reverse case (_after invocation_).

The final piece of the flow is annotating with our custom `@InterceptorBinding` annotation type the method we want to intercept:
```java
// AuditedService.java
@Logged // mark for interception
public void auditedMethod() {
    logger.log(Level.INFO, "Auditing...");
}
```

During the invocation of this method, the CDI checks for any `@Interceptor` bean which is annotated also with the `@Logged` annotation and then proceeds with the flow.

### Events

Check out `EventBean.java` and follow the flow. There are multiple types of events that will be explaned below. <br>
Check the runtime logs: [Login Page](http://localhost:8080/java-ee-0.0.1-SNAPSHOT/login.xhtml)

#### Plain events (no qualifiers)

```java
@Inject
Event<EventData> plainEvent;

plainEvent.fire(new EventData(user.getEmail(), LocalDateTime.now()));
```

EventData.java is a custom class that is used when firing an event. The events are handled by observers:
```java
  // EventObserver.java  
  void plainEvent(@Observes EventData eventData) {
    logger.log(Level.INFO, "User {0} logged in at {1}. Logged from plain event observer",
            new Object[]{eventData.getEmail(), eventData.getLoginTime()});
  }
```

#### Qualifier Events

```java
@Inject
@PopularStand
private Event<EventData> eventDataEvent;

eventDataEvent.fire(new EventData(user.getEmail(), LocalDateTime.now()));

// EventObserver.java  
void userLoggedIn(@Observes @PopularStand EventData eventData) {
  logger.log(Level.INFO, "User {0} logged in at {1}. Logged from qualified observer",
          new Object[]{eventData.getEmail(), eventData.getLoginTime()});
}
```
Notice the qualifier `@PopularStand`, both in the injection and after configuring the observer for a particular type.

#### Priority Events

Events can be triggered in turns based on priorities: (check out the `EventPriority.java`)
```java
void greetingReceiver1(@Observes @Priority(Interceptor.Priority.APPLICATION + 200) String greeting) {
  logger.log(Level.INFO, "Greeting 1 with lower priority invoked with message " + greeting + "1");
}

//Higher priority
void greetingReceiver2(@Observes @Priority(Interceptor.Priority.APPLICATION) String greeting) {
  logger.log(Level.INFO, "Greeting 2 with higher priority invoked with message " + greeting + "2");
}
```

#### Async Events

Non blocking when firing events, return type is  CompletionStage<T> which can optionally be handled: (the same event publisher is used as in the qualifier case)

```java

// notice the fireAsync method
CompletionStage<EventData> fireAsync = eventDataEvent.fireAsync(new EventData(user.getEmail(), LocalDateTime.now()));

// simulating delay
void asyncObserver(@ObservesAsync @PopularStand EventData eventData) {
  try {
    Thread.sleep(6000);
    logger.log(Level.INFO, "User {0} logged in at {1}. Logged from async observer",
            new Object[]{eventData.getEmail(), eventData.getLoginTime()});
  } catch (InterruptedException e) {
    logger.log(Level.SEVERE, null, e);
  }
}
```

### JPA

This section will provide both the JPA connection configurations, and the application server jdbc connections pool.

#### `persistence.xml`

The file looks like below, the traditional path is: under _resources/META-INF/persistence.xml_
```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.1" xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd">
    <persistence-unit name="postgres" transaction-type="JTA">
        <jta-data-source>jdbc/postgresDataSource</jta-data-source> <!-- Specify the JNDI name of the datasource -->
        <properties>
            ...
             ...
        </properties>
    </persistence-unit>
</persistence>
```
As you can see, under the persistence-unit tag there is a jta-data-source which holds the value of the JNDI. <br>
This JNDI is further configured in the payara application server, with properties like database name, username password etc.. <br>

The JNDI used in this case is a custom created one, but you can always use the default JNDI's that payara offers like below:
```xml
<!--  default payara jndi:  -->
<persistence-unit name="default" transaction-type="JTA">
        <jta-data-source>java:comp/DefaultDataSource</jta-data-source>
```
This is a h2 connection type.

#### Connections Pool

##### File configurations
The connections pool is managed in the payara admin console: [Payara Admin Console](http://localhost:4848/console) _(default credentials are admin:admin)_ <br>
The _domain.xml_ file is manipulated after adding a jdbc resource or connection pool through the interface, but in this case I just followed the pattern of the DefaultDataSource
and added the tags manually since I am running in a dockerized environment and I needed everything to be set-up beforehand. <br>

Required tags:
```xml
<!-- add postgres configs -->
<jdbc-resource enabled="true" jndi-name="jdbc/postgresDataSource" pool-name="PostgresPool"></jdbc-resource>

<!-- add postgres configs -->
<jdbc-connection-pool name="PostgresPool" datasource-classname="org.postgresql.ds.PGSimpleDataSource" res-type="javax.sql.DataSource">
    <property name="ServerName" value="postgres"/>
    <property name="PortNumber" value="5432"/>
    <property name="DatabaseName" value="postgres"/>
    <property name="User" value="postgres"/>
    <property name="Password" value="postgres"/>
    <property name="URL" value="jdbc:postgresql://postgres:5432/postgres"/>
</jdbc-connection-pool>

<servers>
    <server config-ref="server-config" name="server">
        ...
         ...
        <resource-ref ref="jdbc/postgresDataSource"></resource-ref>
          ...
```

This file overwrites the existing one in the payara-server by using docker volumes:
```docker
    volumes:
      - ./domain.xml:/opt/payara/appserver/glassfish/domains/domain1/config/domain.xml
      - ./postgresql-42.2.18.jar:/opt/payara/appserver/glassfish/domains/domain1/lib/postgresql-42.2.18.jar
```
The postgresql-42.2.18.jar can be updated with a newer version, but it is necessary for the .java classes to establish the postgres connection:

```xml
...
 ...
<jdbc-connection-pool ... datasource-classname="org.postgresql.ds.PGSimpleDataSource" ...
  ...
   ...
```

##### Script Init
Another way is through config scripts, such as the `configScript.sh`. <br>
The comments inside the script file will serve as a guide, but the idea is to not use file configs, rather initialize the JNDI configurations through payara commands.

In the docker-compose we have map the script and execute it:
```yaml
    volumes:
      #- ./domain.xml:/opt/payara/appserver/glassfish/domains/domain1/config/domain.xml
      - ./configScript.sh:/opt/payara/configScript.sh
      - ./passwordfile:/opt/payara/passwordfile
      - ./postgresql-42.2.18.jar:/opt/payara/appserver/glassfish/domains/domain1/lib/postgresql-42.2.18.jar
      - ./target/java-ee-0.0.1-SNAPSHOT.war:/opt/payara/deployments/java-ee-0.0.1-SNAPSHOT.war
    entrypoint: ["/bin/sh", "-c", "chmod +x /opt/payara/configScript.sh && /opt/payara/configScript.sh"]
```
