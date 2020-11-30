#  Testing Jakarta EE 9 components with Arquillian and Weld

[Arquillian](http://www.arquillian.org) (JBoss Arquillian) has began to add JUnit5 and Jakarta EE 9 support, currently the official maintained modules are aligning with the changes since 1.7.0. 

For impatient developers, you can try to run your Jakarta EE 9/JUnit 5 based Arquillian tests against Weld container, Glassfish v6 (both managed and  remote) and Apache Tomcat 10 (for Jakarta Servlet 5.0).

In this post, we will try to run the tests on the Weld container. 

## Prerequisites

* Java 8 or Java 11 ([OpenJDK](https://openjdk.java.net/install/) or [AdoptOpenJDK](https://adoptopenjdk.net/installation.html))
* The latest [Apache Maven](http://maven.apache.org/download.cgi)
* The basic knowledge of [JUnit 5](https://junit.org/junit5/)
* Get to know [the basic of Arquillian](http://arquillian.org/guides/)

## Configuring Arquillian and JUnit 5

Add Junit 5 dependencies to your project *pom.xml* file.

```xml
<dependencyManagement>
    <dependencies>
        ...
        <dependency>
            <groupId>org.junit</groupId>
            <artifactId>junit-bom</artifactId>
            <version>5.7.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    ...
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

Add the new Arquillian JUnit 5 integration dependency.

```xml
...
<dependency>
    <groupId>org.jboss.arquillian.junit5</groupId>
    <artifactId>arquillian-junit5-container</artifactId>
    <scope>test</scope>
</dependency>
```
And switch to use the latest Jakarta Servlet 5.0 protocol.

```xml
<dependency>
    <groupId>org.jboss.arquillian.protocol</groupId>
    <artifactId>arquillian-protocol-servlet-jakarta</artifactId>
</dependency>
```

Declare the version used in the *properties* section.

```xml
<arquillian-bom.version>1.7.0.Alpha5</arquillian-bom.version>
<junit-jupiter.version>5.7.0</junit-jupiter.version>
```

By default, when `arquillian-protocol-servlet-jakarta` is ocurred in the classpath, the **Servlet 5.0** protocol will be the default protocol, if you have declared the protocol in the existing *arquillian.xml* file, change it to use **Servlet 5.0**.

```xml
<arquillian xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://jboss.org/schema/arquillian"
            xsi:schemaLocation="http://jboss.org/schema/arquillian
    http://jboss.org/schema/arquillian/arquillian_1_0.xsd">
    <defaultProtocol type="Servlet 5.0"/>
    
</arquillian>    
```

## Configuring Arquillian Weld Container 

Add the newest `arquillian-weld-embedded` dependency into the project *pom.xml* file. 

```xml
<dependency>
    <groupId>org.jboss.arquillian.container</groupId>
    <artifactId>arquillian-weld-embedded</artifactId>
    <version>${arquillian-weld-embedded.version}</version>
    <scope>test</scope>
</dependency>
```
And also add the weld runtime into dependencies.

```xml
<dependency>
    <groupId>org.jboss.weld</groupId>
    <artifactId>weld-core-impl</artifactId>
    <version>${weld.version}</version>
    <scope>test</scope>
</dependency>
```

In the Arquillian configuration file `arquillian.xml`, add a container specific configuration for Weld.

```xml
<container qualifier="arq-weld">
    <configuration>
    <property name="enableConversationScope">true</property>
    <property name="environment">SE</property>
    </configuration>
</container>
```

The `enableConversationScope` property allows you decide if use `@ConversationScope` in the tests. And there are several environment available, check the source code of [Environments](https://github.com/weld/api/blob/master/weld-spi/src/main/java/org/jboss/weld/bootstrap/api/Environments.java).

As a CDI container, we can not run all Jakarta EE compoennts with Weld, for example, the Jakarta Restul WebService resources.

So let's exclude the components that can not be run in the Weld containers, and focus on  testing CDI components.

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>${maven-failsafe-plugin.version}</version>
    <configuration>
        <systemPropertyVariables>
        	<arquillian.launch>arq-weld</arquillian.launch>
        </systemPropertyVariables>
        <excludes>
        	<exclude>**/it/GreetingResourceTest*</exclude>
        </excludes>
    </configuration>
</plugin>
```
In [our sample project](https://github.com/hantsy/jakartaee9-starter-boilerplate), there are two integration tests will be run on Arquillian containers, let's exclude the `GreetingResourceTest` which is use to expose Restful APIs.

The left is `GreetingServiceTest`,  which is to test the functionality of a simple CDI bean  `GreetingService`.

```java
@ExtendWith(ArquillianExtension.class)
public class GreetingServiceTest {
    private final static Logger LOGGER = Logger.getLogger(GreetingServiceTest.class.getName());

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClass(GreetingMessage.class)
                .addClass(GreetingService.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    GreetingService service;

    @Test
    @DisplayName("testing buildGreetingMessage")
    public void should_create_greeting() {
        LOGGER.log(Level.INFO, " Running test:: GreetingServiceTest#should_create_greeting ... ");
        GreetingMessage message = service.buildGreetingMessage("Jakarta EE");
        assertTrue(message.getMessage().startsWith("Say Hello to Jakarta EE at "),
                "message should start with \"Say Hello to Jakarta EE at \"");
    }
}
```
In the above codes,

* The new `@ExtendWith(ArquillianExtension.class)` replaces the old `@RunWith(Arquilian.class)` in [the Jakarta EE 8 version](https://github.com/hantsy/jakartaee8-starter).
* The `@Test` annotation is from the `org.junit.jupiter.api` package which belongs to JUnit 5.
* The `@DisplayName` annotation allows IDEs or other tools to use a friendly describable text instead of the method name in the test reporting results.
* The `@Deployment` describes the assembly resources of the deployment archive for this test. Similar with general Jakarta EE components, you can use `@Inject` beans in Arquillian test classes.  These two items are no difference from [the Jakarta EE 8 starter](https://github.com/hantsy/jakartaee8-starter).

## Running tests

Open a terminal window, enter the project root folder, execute the following command.

```bash
mvn clean verify -Parq-weld
```
In [the Jakarta EE 9 sample project](https://github.com/hantsy/jakartaee9-starter-boilerplate), I usually use a Maven profile to organize the resources of testing against a Arquillian container. Here  `arq-weld` profile is configured for Weld container.

>NOTE: In the real world application development, it is an excellent practice to use Maven profiles to categorize the configurations for different environments.

You will see the following info in the console.

```bash
INFO:  Running test:: GreetingServiceTest#should_create_greeting ...
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.783 s - in com.example.it.GreetingServiceTest
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO]
[INFO] --- maven-failsafe-plugin:3.0.0-M5:verify (integration-test) @ jakartaee9-starter-boilerplate ---
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  11.553 s
[INFO] Finished at: 2020-11-30T19:17:50+08:00
[INFO] ------------------------------------------------------------------------
```
