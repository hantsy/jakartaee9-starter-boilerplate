#  Testing Jakarta EE 9 Applications with Arquillian and Open Liberty

The [OpenLiberty/arquillian-liberty](https://github.com/OpenLiberty/liberty-arquillian) has began to [add Jakarta EE 9 support](https://github.com/OpenLiberty/liberty-arquillian/issues/71). For impatient users, you can taste the current work in your project now.

In this post, we will try to run the our tests on the Open Liberty container using both managed and remote adapters. 

## Prerequisites

* Java 8 or Java 11 ([OpenJDK](https://openjdk.java.net/install/) or [AdoptOpenJDK](https://adoptopenjdk.net/installation.html))
* The latest [Apache Maven](http://maven.apache.org/download.cgi)
* The basic knowledge of [JUnit 5](https://junit.org/junit5/)
* Get to know [the basic of Arquillian](http://arquillian.org/guides/)

Before adding Open Liberty and Aquilian integration configuration into your project, please make sure you have added [Arquillian Jarkarta EE 9 and JUnit 5 dependencies](./docs/arq-weld.md). 

## Configuring OpenLiberty Managed Container Adapter

Add `arquillian-liberty-managed-jakarta` dependency into your project. 

```xml
<dependency>
    <groupId>io.openliberty.arquillian</groupId>
    <artifactId>arquillian-liberty-managed-jakarta</artifactId>
    <version>${arquillian-liberty-jakarta.version}</version>
</dependency>
```
Define the `arquillian-liberty-jakarta.version` property in the *properties* section.

```xml
<arquillian-liberty-jakarta.version>2.0.0-M1</arquillian-liberty-jakarta.version>
```

> NOTE:  For the Jakarta EE 9,  the  [arquillian-liberty](https://github.com/OpenLiberty/liberty-arquillian)  project uses a new namespace,  please note the groupId(`arquillian-liberty-managed-jakarta `), it has a **-jakarta** postfix. 

If you are using Jakarta Restful WS Client in your test codes to shake hands with your Restful Web Services, you should add a Restful WS Client implementation into the **test** scope.  But I found the Open Liberty 21.0.0.1-beta is a little different from the previous Jakarta EE 8 compatible version, the Restful WS implementation is changed to Resteasy. But Resteasy still does not provide a public version for Jakarta EE 9. Till now, only Jersey completed the Jakarta EE 9 transformation. 

So add the **jersey-client** related dependencies.

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

Create  a container configuration in the *arquillian.xml* file.

```xml
<container qualifier="liberty-managed">
    <configuration>
        <property name="wlpHome">target/wlp/</property>
        <property name="serverName">defaultServer</property>
        <property name="httpPort">9080</property>
        <property name="serverStartTimeout">300</property>
    </configuration>
</container>
```
You can configure a *wlpHome* to use the existing *Open Liberty* server in your local system.

To get a clean environment to run your tests every time,  I use a **Dependency Maven Plugin** to download a copy of Open Liberty, and use it to run your tests. It is good to automate the progress in a CI server.

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
                        <groupId>io.openliberty.beta</groupId>
                        <artifactId>openliberty-runtime</artifactId>
                        <version>${liberty.runtime.version}</version>
                        <type>zip</type>
                        <overWrite>false</overWrite>
                        <outputDirectory>${project.build.directory}</outputDirectory>
                    </artifactItem>
                    <artifactItem>
                        <groupId>io.openliberty.arquillian</groupId>
                        <artifactId>arquillian-liberty-support-jakarta</artifactId>
                        <version>${arquillian-liberty-jakarta.version}</version>
                        <type>zip</type>
                        <classifier>feature</classifier>
                        <overWrite>false</overWrite>
                        <outputDirectory>${project.build.directory}/wlp/usr</outputDirectory>
                    </artifactItem>
                </artifactItems>
            </configuration>
        </execution>
    </executions>
</plugin>
<plugin>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>${maven-failsafe-plugin.version}</version>
    <configuration>
        <systemPropertyVariables>
            <arquillian.launch>liberty-managed</arquillian.launch>
            <java.util.logging.config.file>${project.build.testOutputDirectory}/logging.properties
            </java.util.logging.config.file>
        </systemPropertyVariables>
    </configuration>
</plugin>

```

Use a  Open Liberty *server.xml* for test purpose.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<server description="new server">

    <!-- Enable features -->
    <featureManager>
        <feature>jakartaee-9.0</feature>
        <feature>usr:arquillian-support-jakarta-2.0</feature>
        <feature>localConnector-1.0</feature>
    </featureManager>
    
    <!-- To access this server from a remote client add a host attribute to the following element, e.g. host="*" -->
    <httpEndpoint id="defaultHttpEndpoint"
                  httpPort="9080"
                  httpsPort="9443" />
                  
    <!-- Automatically expand WAR files and EAR files -->
    <applicationManager updateTrigger="mbean"  autoExpand="true"/>

</server>
```

To make our Open Liberty arquillian adapter work, you have to configure a `localConnector` feature, `arquillian-support-jakarta-2.0` is helpful in development and used to gather more concise exception info from Open Liberty when running the tests.

Execute the following command  to run the tests.

```bash
mvn clean verify -Parq-libery-managed
```


## Configuring Open Liberty Remote Container Adapter

With Open Liberty Remote Container Adapter, you can run the tests against a running Open Liberty server, esp. it is running on a different server. 

Adding the following `arquillian-liberty-remote-jakarta` dependency  instead.

```xml
<dependency>
    <groupId>io.openliberty.arquillian</groupId>
    <artifactId>arquillian-liberty-remote-jakarta</artifactId>
    <version>${arquillian-liberty-jakarta.version}</version>
</dependency>
// the jersey client depdenceies are omitted.
```

And add a *container* section in the *arquillian.xml* file for this remote container.

```xml
<container qualifier="liberty-remote">
        <configuration>
            <property name="hostName">localhost</property>
            <property name="serverName">testServer</property>
            <property name="username">admin</property>
            <property name="password">admin</property>
            <property name="httpPort">9080</property>
            <property name="httpsPort">9443</property>
        </configuration>
    </container>
```

By default, when you start Open Liberty at the first time, it will create a new *defaultServer*  configuration.

For the test purpose, we create a new *testServer*.

```bash
server create testServer
```

Replace the generated server.xml in *[Open Liberty dir]/usr/servers/testServer/server.xml* with following content. 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<server description="Jakarta EE 9 test server">

    <!-- Enable features -->
    <featureManager>
        <feature>jakartaee-9.0</feature>
        <feature>restConnector-2.0</feature>
    </featureManager>

    <quickStartSecurity userName="admin" userPassword="admin" />

    <!-- Default SSL configuration enables trust for default certificates from the Java runtime -->
    <ssl id="defaultSSLConfig" trustDefaultCerts="true"/>

    <keyStore id="defaultKeyStore" password="password" location="key.jks" type="JKS"/>

    <httpEndpoint id="defaultHttpEndpoint"
                  httpPort="9080"
                  httpsPort="9443" host="*"/>

    <!-- Automatically expand WAR files and EAR files -->
    <applicationManager autoExpand="true" updateTrigger="mbean" />
    
    <logging consoleLogLevel="INFO" />
        <writeDir>${server.config.dir}/dropins</writeDir>
    </remoteFileAccess>
    
</server>
```

Start the server, it will prepare the required   resources defined in the *server.xml* file.

```bash
# start server testServer
server start testServer
```
For remote connections, it depends on `restConnector-2.0` feature, and it enabled the SSL connection. 

To avoid the SSL certificate exception when running our tests, you should export the certificate from the running OpenLiberty server, and import it into the client JVM certs, as we done in the [Testing with Arquillian and Glassfish](./docs/arq-glassfish.md).


```bash
keytool -export -alias default -file testwlp.crt -keystore [Open Liberty install dir]/usr/servers/testServer/reources/security/key.jks

keytool -import -trustcacerts -keystore ${JAVA_HOME}/jre/lib/security/cacerts -storepass changeit -alias testwlp -file testwlp.crt -noprompt
```

>Before executing the tests, make sure the target Open Liberty server is running.

Execute  the following command to run tests .

```bash
mvn clean verfiy -Parq-liberty-managed
```

Grab a copy of  [the source codes from my Github](https://github.com/hantsy/jakartaee9-starter-boilerplate), and explore  yourself.

