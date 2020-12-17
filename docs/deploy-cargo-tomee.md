# Deploying Jakarta EE 9 applications to Apache TomEE using Cargo maven plugin

 We have discussed the [deployment to Apache TomEE](./deploy-tomee.md) using the official tomee-maven-plugin. With tomee-maven-plugin, it is easy to download a copy of Apache TomEE distribution and start the TomEE server and then deploy the application to it. Or deploy your application to a running TomEE server.  The official maven plugin is great for most cases, but it lacks the ability to deploy the application to a local installed server with fine-grained configurations.

 Cargo maven plugin 1.8.3 brought updates to TomEE 9.0 which is aligned to Jakarta EE 9, there are several deployment options.

* Deploy to a local TomEE server with a standalone configuration
* Deploy to a local TomEE server with a existing configuration
* Deploy to a running TomEE server with a runtime configuration

## Prerequisites

Make sure you have installed the following software.

* Java 8 or Java 11 ([OpenJDK](https://openjdk.java.net/install/) or [AdoptOpenJDK](https://adoptopenjdk.net/installation.html))
* [Apache TomEE (Plume) 9.0.0-M3](http://tomee.apache.org/download-ng.html)
* [Apache Maven](http://maven.apache.org/) 
* Get to know the basic of [Cargo maven plugin](https://codehaus-cargo.github.io/). 

## Deploy to local TomEE server

The following configuration is using a local installed server with a **standalone** configuration.

```xml
<profile>
    <id>tomee-local</id>
    <properties>
        <tomee.home>${project.build.directory}/apache-tomee-plume-${tomee.version}</tomee.home>
    </properties>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <executions>
                    <execution>
                        <id>unpack</id>
                        <phase>process-resources</phase>
                        <goals>
                            <goal>unpack</goal>
                        </goals>
                        <configuration>
                            <artifactItems>
                                <artifactItem>
                                    <groupId>org.apache.tomee.jakarta</groupId>
                                    <artifactId>apache-tomee</artifactId>
                                    <version>${tomee.version}</version>
                                    <classifier>plume</classifier>
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
                <groupId>org.codehaus.cargo</groupId>
                <artifactId>cargo-maven2-plugin</artifactId>
                <configuration>
                    <container>
                        <containerId>tomee9x</containerId>
                        <type>installed</type>
                        <home>${tomee.home}</home>
                    </container>
                    <configuration>
                        <type>standalone</type>
                        <properties>
                        </properties>
                    </configuration>
                </configuration>
            </plugin>
        </plugins>
    </build>
</profile>

```

In this configuration, we use dependency maven plugin to prepare a  TomEE server for you, of course you can use the local existing server instead.

In the `container` section, used a **installed** type, and specify the *home* to the **location of the TomEE server**.

In the `configuration` section, we use a  **standalone** configuration here.

> More details about the TomEE properties, see the [Cargo TomEE 9.x container page](https://codehaus-cargo.github.io/cargo/TomEE+9.x.html).

Run the following command to deploy our application.

```bash
mvn clean package cargo:run -Ptomee-local
```

It will perform a series of tasks, including:

* Build the project and packaged it into a war in the target folder.
* Download a copy of Apache TomEE, and extract the files to the target folder. 
* Then create a new **standalone** configuration in the target folder which is only use for this application.
* Start the TomEE server using the created **standalone** configuration.
* Finally deploy the war to the running server.

> By default, Apache Tomcat and Apache TomEE do not contain configurations for multi domains/instances, but it is possible, there are lots of articles in [Google search results](https://www.google.com/search?client=firefox-b-d&q=tomcat+multi+instance). The cargo generated **standalone** configuration is an example for configuring a new instance.

If you want to reuse the default configuration in the TomEE server, change the `configuraiton` section to the following.

```xml
<configuration>
    <type>existing</type>
    <home></home>
</configuration>
```

In this configuration, set the type value to **existing**, and the *home* is same as home in the `container` section, because there is no specifical configurations for domain instances like Glassfish. 

To undeploy the application, just send a `CTRL+C` to the console.

## Deploy to a running TomEE server

To deploy to a running TomEE server, esp. it is located in a different host, using the following configuration instead.

```xml
<profile>
    <id>tomee-remote</id>
    <!-- Add `manager-script` role to the tomee user in tomee/conf/tomcat-users.xml -->
    <!-- Run `mvn clean cargo:deploy -Ptomee-remote` to the running TomEE-->
    <build>
        <plugins>
            <plugin>
                <groupId>org.codehaus.cargo</groupId>
                <artifactId>cargo-maven2-plugin</artifactId>
                <configuration>
                    <container>
                        <containerId>tomee9x</containerId>
                        <type>remote</type>
                    </container>
                    <configuration>
                        <type>runtime</type>
                        <properties>
                            <cargo.remote.username>tomee</cargo.remote.username>
                            <cargo.remote.password>tomee</cargo.remote.password>
                        </properties>
                    </configuration>
                </configuration>
            </plugin>
        </plugins>
    </build>
        </profile>
```

 In this configuration, in the `container` section, set the type to **remote**, and in the `configuration` section set type to `runtime`, and do not forget set username and password used to connect the remote server.

Similar to Apache Tomcat,  Cargo also requires the configured user has a **manager-script** role. Open the *tomee dir/conf/tomcat-users.xml* file, make sure **manager-script** is set in the roles.

```xml
<user username="tomee" password="tomee" roles="tomee-admin,manager-gui,manager-script" />
```

Run the following command to deploy and undeploy the applications.

```bash
# deploy applications
mvn clean package cargo:deploy -Ptomee-remote

# undeploy
mvn cargo:undeploy -Ptomee-remote
```


