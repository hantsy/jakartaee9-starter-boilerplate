#  Testing Jakarta EE 9 Applications with Arquillian and WildFly

As the long-awaited [Jakarta EE 9 support](https://issues.redhat.com/projects/WFARQ/issues/WFARQ-89) issue was fixed in the newest 5.0.0.Alpha1 version of [WildFly Arquillian project](https://github.com/wildfly/wildfly-arquillian),  finally we can test Jakarta EE 9 applications against WildFly application server with the newest Arquillian adapters. 

In this post, we will explore testing Jakarta EE 9 application with various  WildFly Arquillian adapters.

* *WildFly Arquillian Managed Container*, in this case the Arquillian controller is responsible for starting and stopping the WildFly server via administrative tools.
* *WildFly Arquillian Remote Container*, Arquillian does not start and stop the WildFly server, it deploys the test archive to a running WildFly server through available remote protocols, such as HTTP, JMX etc.
* *WildFly Arquillian Embedded Container*, similar to the managed adapter, but embedded adapter controls the WildFly server via embedded WildFly APIs, generally the test itself and the server would run on the same thread.
* *WildFly Arquillian Domain Managed Container*, use a single domain instead of the standalone server.
* *WildFly Arquillian Domain Remote Container*, use a single domain instead of the standalone server.
* *WildFly Arquillian Bootable JAR Container*, it deploys the test archive on the bootable JAR built by maven bootable jar plugin.

Here we focus on the first three adapters.

## Prerequisites

* Java 11 ([OpenJDK](https://openjdk.java.net/install/) or [AdoptOpenJDK](https://adoptopenjdk.net/installation.html))
* The latest [Apache Maven](http://maven.apache.org/download.cgi)
* The basic knowledge of [JUnit 5](https://junit.org/junit5/)
* Get to know [the basic of Arquillian](http://arquillian.org/guides/)
* Download a copy of [WildFly](https://www.wildfly.org) server, please download the *Jakarta EE 9 tech preview* version.

## Code Examples

Get [the source codes from my Github](https://github.com/hantsy/jakartaee9-starter-boilerplate), and read [the docs](https://hantsy.github.io/jakartaee9-starter-boilerplate/) to understand the project structure and  the existing source codes.  We will focus on the  Arquillian configurations for these WildFly adapters.

## Configuring WildFly Managed Container Adapter

Add the following dependencies into your project, the Jersey related dependencies are used for test RESTful APIs using *JAXRS Client* API.  WildFly includes its built-in JAXRS implementation - RESTEasy(maybe transformed by Jakarta EE 9 migration tools),  but currently there is no public official RESTEasy versions released for Jakarta EE 9.

```xml
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
<dependency>
    <groupId>org.wildfly.arquillian</groupId>
    <artifactId>wildfly-arquillian-container-managed</artifactId>
    <version>${wildfly-arquillian.version}</version>
    <scope>test</scope>
</dependency>
```
Create  a container configuration in the *arquillian.xml* file.

```xml
<container qualifier="wildfly-managed">
    <configuration>
        <!--<property name="jbossHome">${jbossHome:target/wildfly-18.0.1.Final}</property>-->
        <property name="serverConfig">standalone-full.xml</property>
    </configuration>
</container>
```
You can define a `jbossHome` property in the *arquillian.xml* file to specify the location of the existing WildFly server in your local system.

Alternatively, to get a clean environment to run your tests every time,  you can configure a **Dependency Maven Plugin** to download a copy of WildFly, and use it to run your tests. This is a great option to run the tests continuously in your CI servers.

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
                        <groupId>org.wildfly</groupId>
                        <artifactId>wildfly-preview-dist</artifactId>
                        <version>${wildfly.version}</version>
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
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>${maven-failsafe-plugin.version}</version>
    <configuration>
        <environmentVariables>
            <JBOSS_HOME>${project.build.directory}/wildfly-preview-${wildfly.version}</JBOSS_HOME>
        </environmentVariables>
        <systemPropertyVariables>
            <arquillian.launch>wildfly-managed</arquillian.launch>
            <serverConfig>standalone-full.xml</serverConfig>
            <java.util.logging.manager>org.jboss.logmanager.LogManager</java.util.logging.manager>
        </systemPropertyVariables>
    </configuration>
</plugin>
```

> Note: we use `wildfly-preview-dist` artifactId here to download the WildFly Jakarta EE 9 preview version.

Open your terminal and switch to the project root folder.

Run the following command to run the tests against WildFly server.

```bash
mvn clean verify -Parq-wildfly-managed
```


## Configuring WildFly Remote Container Adapter

With Arquillian WildFly Remote Container adapter, you can run the tests against a running WildFly server, esp. it is running on a different host. 

Adding the following `wildfly-arquillian-container-remote` dependency  instead.

```xml
<dependency>
    <groupId>org.wildfly.arquillian</groupId>
    <artifactId>wildfly-arquillian-container-remote</artifactId>
    <version>${wildfly-arquillian.version}</version>
    <scope>test</scope>
</dependency>
// the jersey client depdenceies are omitted.
```

And add a *container* section in the *arquillian.xml* file for this remote container, and configure the essential properties, such as  `username`, `password`, `protocol`etc. 

```xml
<container qualifier="wildfly-remote">
    <configuration>
        <property name="managementAddress">127.0.0.1</property>
        <property name="managementPort">9990</property>
        <property name="protocol">http-remoting</property>
        <property name="username">admin</property>
        <property name="password">Admin@123</property>
    </configuration>
</container>
```

Before executing the tests, setup a admin user configured in the above codes.  Go to the WildFly installation folder, run the  following command to enable the user *admin*.

```bash
<WF_HOME>/bin/add-user.sh admin Admin@123 --silent
```

And make sure the target WildFly server is running.

Execute  the following command to run tests against the running WildFly server.

```bash
mvn clean verfiy -Parq-wildfly-managed
```

### Configuring WildFly Embedded Container Adapter

The configuration of the WildFly embedded adapter is similar to the WildFly managed adapter.

Adding the following `wildfly-arquillian-container-embedded` dependency  instead.

```xml
<dependency>
    <groupId>org.wildfly.arquillian</groupId>
    <artifactId>wildfly-arquillian-container-embedded</artifactId>
    <version>${wildfly-arquillian.version}</version>
    <scope>test</scope>
</dependency>
// the jersey client depdenceies are omitted.
```

Configure a `container`for this WildFly Embedded adapter  in  the *arquillian.xml* file.

```xml
<container qualifier="wildfly-embedded">
    <configuration>
        <!--<property name="jbossHome">${jbossHome:target/wildfly-18.0.1.Final}</property>-->
        <property name="serverConfig">standalone-full.xml</property>
    </configuration>
</container>
```

Similarly, use maven dependency plugin to download a copy of WildFly dist as the runtime server.

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
                        <groupId>org.wildfly</groupId>
                        <artifactId>wildfly-preview-dist</artifactId>
                        <version>${wildfly.version}</version>
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
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>${maven-failsafe-plugin.version}</version>
    <configuration>
        <environmentVariables>
            <JBOSS_HOME>${project.build.directory}/wildfly-preview-${wildfly.version}</JBOSS_HOME>
        </environmentVariables>
        <systemPropertyVariables>
            <arquillian.launch>wildfly-embedded</arquillian.launch>
            <java.util.logging.manager>org.jboss.logmanager.LogManager</java.util.logging.manager>
        </systemPropertyVariables>
    </configuration>
</plugin>
```

Run tests against WildFly Embedded adapter.

```bash
mvn clean verfiy -Parq-wildfly-embedded
```

Grab a copy of  [the source codes from my Github](https://github.com/hantsy/jakartaee9-starter-boilerplate), and exploring them yourself.

