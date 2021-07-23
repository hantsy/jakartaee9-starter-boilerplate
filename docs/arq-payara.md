#  Testing Jakarta EE 9 Applications with Arquillian and Payara 6

In the latest Payara 5, it added early Jakarta EE 9 support. Payara 6.2021.Alpha1 brought full Jakarta EE 9/9.1 support by default. As [ Arquillian support for Payara 6 issue](https://github.com/payara/ecosystem-support/issues/28) was fixed, testing Jakarta EE 9 applications against Payara servers is available.

Payara Arquillian  project provides several container adapters.

* Payara Managed Container Adapter
* Payara Remote Container Adapter
* Payara Embedded Container Adapter
* Payara Micro Managed Container Adapter



## Example Codes

The [example codes](https://github.com/hantsy/jakartaee9-starter-boilerplate) are shared via my Github. There are 4 Maven profiles provided in the project *pom.xml* file for these 4 adapters.

## Prerequisites

* Java 11 ([OpenJDK](https://openjdk.java.net/install/) or [AdoptOpenJDK](https://adoptopenjdk.net/installation.html))
* The latest [Apache Maven](http://maven.apache.org/download.cgi)
* The basic knowledge of [JUnit 5](https://junit.org/junit5/)
* Get to know [the basic of Arquillian](http://arquillian.org/guides/)

## Configuring Payara Managed Container Adapter

Add `arquillian-payara-server-managed` dependency into your project. 

```xml
<dependency>
    <groupId>fish.payara.arquillian</groupId>
    <artifactId>payara-client-ee9</artifactId>
    <version>${arquillian-payara.version}</version>
    <scope>test</scope>
</dependency>
<!-- Payara Server Container adaptor -->
<dependency>
    <groupId>fish.payara.arquillian</groupId>
    <artifactId>arquillian-payara-server-managed</artifactId>
    <version>${arquillian-payara.version}</version>
    <scope>test</scope>
</dependency>
```
Create  a container configuration in the *arquillian.xml* file.

```xml
<container qualifier="payara-managed">
    <configuration>
        <property name="allowConnectingToRunningServer">false</property>
        <property name="adminHost">localhost</property>
        <property name="adminPort">4848</property>
        <property name="enableH2">${enableDerby:true}</property>
        <property name="outputToConsole">true</property>
    </configuration>
</container>
```
Configure  `maven-dependency-plugin` to download a copy of Payara distribution, and use it to run your tests. This is a great option to run the tests continuously in your CI servers.

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
                        <groupId>fish.payara.distributions</groupId>
                        <artifactId>payara</artifactId>
                        <version>${payara.version}</version>
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
        <systemPropertyVariables>
            <payara.home>${project.build.directory}/payara6</payara.home>
            <arquillian.launch>payara-managed</arquillian.launch>
        </systemPropertyVariables>
    </configuration>
</plugin>
```

Run the following command  to run the tests.

```bash
mvn clean verify -Parq-payara-managed
```

In this case, Arquillian is responsible for starting and stopping the Payara container.


## Configuring Payara Remote Container Adapter

With Arquillian Payara remote container, you can run the tests against a running Payara server, esp. it is running on a different host. 

Adding the following `arquillian-payara-server-remote` dependency  instead.

```xml
<dependency>
    <groupId>fish.payara.arquillian</groupId>
    <artifactId>payara-client-ee9</artifactId>
    <version>${arquillian-payara.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>fish.payara.arquillian</groupId>
    <artifactId>arquillian-payara-server-remote</artifactId>
    <version>${arquillian-payara.version}</version>
    <scope>test</scope>
</dependency>
```

And add a *container* section in the *arquillian.xml* file for this remote container, and configure the `adminHost` `adminPort`, `adminUser` , `adminPassword` if they are different from the default value.

```xml
  <container qualifier="payara-remote">
        <configuration>
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

Before executing the tests, make sure the target Payara server is running.

Run the following command to run tests against the running Payara server.

```bash
mvn clean verfiy -Parq-payara-managed
```

## Configuring Payara Embedded Container Adapter

With the  Payara embedded container adapter, you can run tests on an embedded Payara server.

Add the following dependency into your project.

```xml
 <dependency>
     <groupId>fish.payara.extras</groupId>
     <artifactId>payara-embedded-all</artifactId>
     <version>${payara.version}</version>
     <scope>test</scope>
</dependency>
<dependency>
    <groupId>fish.payara.arquillian</groupId>
    <artifactId>arquillian-payara-server-embedded</artifactId>
    <version>${arquillian-payara.version}</version>
    <scope>test</scope>
</dependency>
```

Then run the following command to run tests.

```bash
mvn clean verfiy -Parq-payara-embdded
```

## Configuring Payara Micro Managed Container Adapter

With the  Payara Micro managed container adapter, you can run tests on an Payara Micro fat JAR.

Add the following dependency into your project.

```xml
 <dependency>
      <groupId>fish.payara.arquillian</groupId>
      <artifactId>payara-client-ee9</artifactId>
      <version>${arquillian-payara.version}</version>
      <scope>test</scope>
</dependency>

<!-- Payara Micro Managed Container Adaptor -->
<dependency>
    <groupId>fish.payara.arquillian</groupId>
    <artifactId>arquillian-payara-micro-managed</artifactId>
    <version>${arquillian-payara.version}</version>
    <scope>test</scope>
</dependency>
```

Download a copy of Payara Micro via  maven dependency plugin, and set a system property `payara.microJar` to point to the location of the downloaded Payara Micro JAR.

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
                <goal>copy</goal>
            </goals>
            <configuration>
                <artifactItems>
                    <artifactItem>
                        <groupId>fish.payara.extras</groupId>
                        <artifactId>payara-micro</artifactId>
                        <version>${payara.version}</version>
                        <type>jar</type>
                        <overWrite>false</overWrite>
                        <outputDirectory>${project.build.directory}</outputDirectory>
                        <destFileName>payara-micro.jar</destFileName>
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
        <systemPropertyVariables>
            <payara.microJar>${project.build.directory}/payara-micro.jar</payara.microJar>
        </systemPropertyVariables>
    </configuration>
</plugin>
```

Then run the following command to run tests.

```bash
mvn clean verfiy -Parq-payara-micro
```

Get  [the source codes from my Github](https://github.com/hantsy/jakartaee9-starter-boilerplate).

