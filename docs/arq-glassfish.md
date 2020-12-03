#  Testing Jakarta EE 9 Applications with Arquillian and Glassfish v6

[Arquillian](http://www.arquillian.org)  added a new module [Arquillian Container Glassfish6](https://github.com/arquillian/arquillian-container-glassfish6) to align with the changes of Jakarta EE 9 and the features introduced in the Arquillian Core 1.7.0.  [Arquillian Container Glassfish6](https://github.com/arquillian/arquillian-container-glassfish6) is designated to run tests on Glassfish v6, which is a full-featured Jakarta EE 9 compatible application server, so you can test all Jakarta EE 9 components using this new Arquillian Glassfish container.

In this post, we will try to run the our tests on the Glassfish container using both managed and remote adapters. 

* When using the managed adapter, Arquillian has ability to manage lifecycle of Glassfish server,eg.  start and stop the container during the testing execution.
* When using the remote adapter, Arquillian will try to run tests against a remote container, and gather the testing report through a proxy and send back to clients(IDE, Maven command console, etc.).

> Note: The original [Aruqillian Glassfish embedded container](https://github.com/arquillian/arquillian-container-glassfish/tree/master/glassfish-embedded-3.1) is not ported to the latest Glassfish v6 now.

## Prerequisites

* Java 8 ([OpenJDK](https://openjdk.java.net/install/) or [AdoptOpenJDK](https://adoptopenjdk.net/installation.html))
* The latest [Apache Maven](http://maven.apache.org/download.cgi)
* The basic knowledge of [JUnit 5](https://junit.org/junit5/)
* Get to know [the basic of Arquillian](http://arquillian.org/guides/)

> Note: Make sure you are using Java 8, Glassfish v6.0 does not support Java 11. Glassfish v6.1 will focus on Java 11 support.
> 

## Testing Restful Web Service

Currently, only two simple Arquillian integration tests are included in our sample project, we have introduced the CDI components in the last post.

Let's move on the `GreetingResourceTest`.

```java
@ExtendWith(ArquillianExtension.class)
public class GreetingResourceTest {
    private final static Logger LOGGER = Logger.getLogger(GreetingResourceTest.class.getName());

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(GreetingMessage.class)
                .addClass(GreetingService.class)
                .addClasses(GreetingResource.class, JaxrsActivator.class)
                // Enable CDI
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @ArquillianResource
    private URL base;

    private Client client;

    @BeforeEach
    public void setup() {
        this.client = ClientBuilder.newClient();
        //removed the Jackson json provider registry, due to OpenLiberty 21.0.0.1 switched to use Resteasy.
    }

    @AfterEach
    public void teardown() {
        if (this.client != null) {
            this.client.close();
        }
    }

    @Test
    public void should_create_greeting() throws MalformedURLException {
        LOGGER.log(Level.INFO, " Running test:: GreetingResourceTest#should_create_greeting ... ");
        final WebTarget greetingTarget = client.target(new URL(base, "api/greeting/JakartaEE").toExternalForm());
        try (final Response greetingGetResponse = greetingTarget.request()
                .accept(MediaType.APPLICATION_JSON)
                .get()) {
            assertThat(greetingGetResponse.getStatus()).isEqualTo(200);
            assertThat(greetingGetResponse.readEntity(GreetingMessage.class).getMessage()).startsWith("Say Hello to JakartaEE");
        }
    }
}

```

In the above codes.

* Use a `@ExtendWith(ArquillianExtension.class)` to extend the default JUnit lifecycle, `@ExtendWith` is newly introduced in JUnit 5 to replace the `@RunWith(...)`.
* A static method annotated with `@Deployment(testable = false)` is for describing the deployment unit. Here `testable = false` indicates this tests is running as client mode, you can not inject beans like before.
* You can get the URL of this application after it is deployed into the target server via a `@ArquillianResource` annotation.
* A test method is annotated with `@Test` annotation.

In the `should_create_greeting` method, it uses Restful WS Client API to shake hands with the Restful APIs. You should add an implementation of the Restful WS Client.  At the moment, only **jersey** completed the transformation.

```xml
<!-- Jersey -->
<dependency>
    <groupId>org.glassfish.jersey.media</groupId>
    <artifactId>jersey-media-sse</artifactId>
    <version>${jersey.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.glassfish.jersey.media</groupId>
    <artifactId>jersey-media-json-binding</artifactId>
    <version>${jersey.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.glassfish.jersey.inject</groupId>
    <artifactId>jersey-hk2</artifactId>
    <version>${jersey.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.glassfish.jersey.core</groupId>
    <artifactId>jersey-client</artifactId>
    <version>${jersey.version}</version>
    <scope>test</scope>
</dependency>
```

In the above code, there are some dependencies added.

* The `jersey-client` is the  implementation of Jakarta Restful WS Client.
* The `jersey-hk2` is the dependency inject engine in **jersey**, which will be replaced by CDI in future.
* The `jersey-media-json-binding` is use for JSON serialization and deserialization using **Jakarta JSON Binding**.
* The `jersey-media-sse` is responsible for handling media type `text/event-stream`.



Before configuring the Arquillian Glassfish container support, please make sure you have added [Arquillian Jarkarta EE 9 and JUnit 5 dependencies](./docs/arq-weld.md). 

## Configuring Glassfish Managed Container Adapter

Add `arquillian-glassfish-managed-6` dependency into your project. 

```xml
<dependency>
    <groupId>org.jboss.arquillian.container</groupId>
    <artifactId>arquillian-glassfish-managed-6</artifactId>
    <version>${arquillian-glassfish6.version}</version>
    <scope>test</scope>
</dependency>
```
Create  a container configuration in the *arquillian.xml* file.

```xml
<container qualifier="arq-glassfish-managed">
    <configuration>
        <property name="allowConnectingToRunningServer">false</property>
        <!--            <property name="glassFishHome">target/${glassfish.home}</property>-->
        <property name="adminHost">localhost</property>
        <property name="adminPort">4848</property>
        <property name="enableDerby">${enableDerby:true}</property>
        <property name="outputToConsole">true</property>
    </configuration>
</container>
```
You can define a `glassFishHome` property in the *arquillian.xml* file to specify the location of the existing Glassfish server in your local system.

Alternatively, to get a clean environment to run your tests every time,  you can configure a **Dependency Maven Plugin** to download a copy of Glassfish, and use it to run your tests. This is a great option to run the tests continuously in your CI servers.

```xml
 <plugin>
     <groupId>org.apache.maven.plugins</groupId>
     <artifactId>maven-dependency-plugin</artifactId>
     <version>${maven-dependency-plugin.version}</version>
     <executions>
         <execution>
             <id>unpack</id>
             <phase>pre-integration-test</phase>
             <goals>
                 <goal>unpack</goal>
             </goals>
             <configuration>
                 <artifactItems>
                     <artifactItem>
                         <groupId>org.glassfish.main.distributions</groupId>
                         <artifactId>glassfish</artifactId>
                         <version>${glassfish.version}</version>
                         <type>zip</type>
                         <overWrite>false</overWrite>
                         <outputDirectory>${project.build.directory}</outputDirectory>
                     </artifactItem>
                 </artifactItems>
             </configuration>
         </execution>
     </executions>
</plugin>
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>${maven-failsafe-plugin.version}</version>
    <configuration>
        <environmentVariables>
            <GLASSFISH_HOME>${project.build.directory}/glassfish6</GLASSFISH_HOME>
        </environmentVariables>
    </configuration>
</plugin>
```

Execute the following command  to run the tests against a Glassfish container that managed by Arquillian. 

```bash
mvn clean verify -Parq-glassfish-managed
```

In the managed mode, Arquillian is responsible for starting and stopping the Glassfish container.


## Configuring Glassfish Remote Container Adapter

With Arquillian Glassfish remote container, you can run the tests against a running Glassfish server, esp. it is running on a different server. 

Adding the following `arquillian-glassfish-remote-6` dependency  instead.

```xml
<dependency>
    <groupId>org.jboss.arquillian.container</groupId>
    <artifactId>arquillian-glassfish-remote-6</artifactId>
    <version>${arquillian-glassfish6.version}</version>
    <scope>test</scope>
</dependency>
// the jersey client depdenceies are omitted.
```

And add a *container* section in the *arquillian.xml* file for this remote container, and configure the `adminHost` `adminPort`, `adminUser` , `adminPassword` if they are different from the default value.

```xml
 <container qualifier="glassfish-remote">
        <configuration>
            <!--Supported property names:
            [adminHttps,
            remoteServerHttpPort,
            libraries,
            type,
            remoteServerAddress,
            target,
            retries,
            remoteServerAdminPort,
            remoteServerAdminHttps,
            adminUser,
            authorisation,
            waitTimeMs,
            adminPort,
            properties,
            adminPassword,
            adminHost]-->
            <property name="adminHost">localhost</property>
            <property name="adminPort">4848</property>
            <property name="adminUser">admin</property>
            <!-- if https is enabled via `asadmin enable-secure-admin` on a remote server -->
            <!-- <property name="adminHttps">true</property>-->
            <!-- if admin password is changed via `asadmin change-admin-password` -->
            <!--<property name="adminPassword">adminadmin</property>-->
            <!-- default is empty -->
            <property name="adminPassword"></property>
        </configuration>
    </container>
```

Before executing the tests, make sure the target Glassfish server is running.

Execute  the following command to run tests against the running Glassfish server.

```bash
mvn clean verfiy -Parq-glassfish-managed
```

### Securing administration 

By default, Glassfish does not set a password for `admin` user, you can change it by executing command `asadmin change-admin-password` on the Glassfish server side, do not forget to change the  value of the `adminPassword`property in the above *arquillian.xml* file.

To deploy applications on a remote Glassfish server, it is better to enable secure connections on the remote Glassfish server.  Execute `asadmin enable-secure-admin` on Glassfish side to enable secure connections. Correspondingly, set the `adminHttps` to **true**  in the *arquillian.xml* file.

Once the `adminHttps` is set, when running the tests, you could  get a SSL certification related exception, the reason is the client JVM can not recognize the certificate used in the Glassfish server.  Try to follow the following steps to to overcome this barrier.

Firstly, export the cert from *[Glassfish install dir]/glassfish/domains/domain1/config/keystore.jks* using `keytool`tools  which is shipped with JDK.

```bash
keytool -export -alias default -file testwlp.crt -keystore [Glassfish install dir]/glassfish/domains/domain1/config/keystore.jks
```

And import it into the JVM you are using to run the tests.

```bash
keytool -import -trustcacerts -keystore ${JAVA_HOME}/jre/lib/security/cacerts -storepass changeit -alias testwlp -file testwlp.crt -noprompt
```

Now run the tests again, the certificate exception should be disappeared.

Grab a copy of  [the source codes from my Github](https://github.com/hantsy/jakartaee9-starter-boilerplate), and exploring them yourself.

