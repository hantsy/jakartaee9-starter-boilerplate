# Deploying Jakarta EE 9 applications to Apache TomEE 

Apache TomEE(aka Apache Tomcat + Java EE/Jakarta EE) is a lightweight open-source Jakarta EE application server. Currently it provides a Jakarta EE 9 preview version in the [download page](http://tomee.apache.org/download-ng.html) which is [converted from the TomEE 8 by the Eclipse Transformer tooling project](https://github.com/apache/tomee-jakarta). 

There are several options of TomEE 9 in the download page, choose a variant according to your requirements, for the details of these variants  go to the [feature comparison](http://tomee.apache.org/comparison.html) page.

> NOTE: Apache TomEE is Web Profile compatible, not an implementation of Full Profile.

Get the [source codes](https://github.com/hantsy/jakartaee9-starter-boilerplate) from my Github.

## Prerequisites 

Make sure you have installed the following software.

* Java 11 ([OpenJDK](https://openjdk.java.net/install/) or [AdoptOpenJDK](https://adoptopenjdk.net/installation.html))
* [Apache Maven 3.6.3](http://maven.apache.org/download.cgi)
* [Apache TomEE (Plume) 9.0.0-M3](http://tomee.apache.org/download-ng.html)

## Manual Deployment

As an example, we select the *plume* version, which includes a super feature collection of  Web Profile . 

Extract the files from the downloaded archive into your local disk. The TomEE file structure is similar to Apache Tomcat.

To start a TomEE server, open a terminal window, enter the TomEE root folder and switch to *bin* folder, execute the following command to start a TomEE server in a separate window.

```bash
# Windows
startup.bat
# or 
catalina.bat start

# Linux like system.
sh ./startup.sh  
```
Or run the TomEE server in the same window by executing the following command.

```bash
# Windows
catalina.bat run

# Linux like system.
sh ./catalina.sh run  
```

You will see the start up progress in the TomEE server console.

```bash
Using CATALINA_BASE:   "D:\appsvr\apache-tomee-plume-9.0.0-M3"
Using CATALINA_HOME:   "D:\appsvr\apache-tomee-plume-9.0.0-M3"
Using CATALINA_TMPDIR: "D:\appsvr\apache-tomee-plume-9.0.0-M3\temp"
Using JRE_HOME:        "D:\jdk11"
Using CLASSPATH:       "D:\appsvr\apache-tomee-plume-9.0.0-M3\bin\bootstrap.jar;D:\appsvr\apache-tomee-plume-9.0.0-M3\bin\tomcat-juli.jar"
Using CATALINA_OPTS:   ""
...
```

Execute the following command  in the project root folder to build the project into a *war* package in the *target* folder.

```bash
mvn clean package
```
To deploy the application,  just copy the *target/jakartaee9-starter-boilerplate.war* to the *[tomee install dir]/webapps* folder.

In the console window, you will see the deployment progress  is started automatically.

```bash
29-Nov-2020 15:40:33.673 INFO [Catalina-utility-1] jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke Deploying web application archive [D:\appsvr\apache-tomee-plume-9.0.0-M3\webapps\jakartaee9-starter-boilerplate.war]
29-Nov-2020 15:40:33.673 INFO [Catalina-utility-1] org.apache.tomee.catalina.TomcatWebAppBuilder.init ------------------------- localhost -> /jakartaee9-starter-boilerplate

...

29-Nov-2020 15:40:37.955 INFO [Catalina-utility-1] org.apache.openejb.server.cxf.rs.CxfRsHttpListener.logEndpoints REST Application: http://localhost:8080/jakartaee9-starter-boilerplate/api                 -> com.example.JaxrsActivator@69c1f1fe
29-Nov-2020 15:40:37.955 INFO [Catalina-utility-1] org.apache.openejb.server.cxf.rs.CxfRsHttpListener.logEndpoints      Service URI: http://localhost:8080/jakartaee9-starter-boilerplate/api/greeting        -> Pojo com.example.GreetingResource
29-Nov-2020 15:40:37.955 INFO [Catalina-utility-1] org.apache.openejb.server.cxf.rs.CxfRsHttpListener.logEndpoints               GET http://localhost:8080/jakartaee9-starter-boilerplate/api/greeting/{name} ->      Response greeting(String)
29-Nov-2020 15:40:38.003 INFO [Catalina-utility-1] jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke Deployment of web application archive [D:\appsvr\apache-tomee-plume-9.0.0-M3\webapps\jakartaee9-starter-boilerplate.war] has finished in [4,330] ms
```

Once it is deployed successfully, use `curl` command to verify the deployment result.

```bash
curl http://localhost:8080/jakartaee9-starter-boilerplate/api/greeting/Hantsy
{"message":"Say Hello to Hantsy at 2020-11-29T15:44:29.252675900"}
```
To undeploy the application, just remove the **war** file  from the *[tomee install dir]/webapps* folder. Wait a second, you will see the undeployment progress in the TomEE console.

To stop the running TomEE server, just send a `CTRL+C`  signal in the TomEE console, or execute the shutdown command in another terminal  window.

```bash
# Linux like system.
[tomee install dir]/bin/shutdown  

# Windows
[tomee install dir]\bin\shutdown.bat
```



## Using TomEE Maven Plugin

Apache TomEE provides a official maven plugin for developers to deploy and undeploy applications to TomEE server in application development stage.

Declare the `tomee-maven-plugin`  under `plugins` in the *pom.xml* file.

```xml
<plugin>
    <groupId>org.apache.tomee.maven</groupId>
    <artifactId>tomee-maven-plugin</artifactId>
    <version>${tomee-maven-plugin.version}</version>
</plugin>
```

Define a `tomee-maven-plugin.version`  property to specify the version of `tomee-maven-plugin` to use.

```xml
 <tomee-maven-plugin.version>8.0.5</tomee-maven-plugin.version>
```

> NOTE: Currently `tomee-maven-plugin` does not provide a preview version for Jakarta EE 9/TomEE 9, but the stable version(8.0.5) works great with the latest TomEE 9 preview version.

By default, `tomee-maven-plugin` will retrieve the latest stable version to serve your application. Add the following properties in the `configuration` of  `tomee-maven-plugin` to force it to use the latest TomEE 9.0.0-M3 instead.

```xml
<!-- download and run on a managed Apache TomEE server -->
<tomeeVersion>${tomee.version}</tomeeVersion>
<tomeeArtifactId>apache-tomee</tomeeArtifactId>
<tomeeGroupId>org.apache.tomee.jakarta</tomeeGroupId>
<tomeeClassifier>plume</tomeeClassifier>
```

Define a `tomeeVersion` to specify the TomEE version.

```xml
 <tomee.version>9.0.0-M3</tomee.version>
```

> NOTE:  The `tomeeGroupId` is **org.apache.tomee.jakarta**, which is different from the stable version,   TomEE 8.0 uses groupId **org.apache.tomee**.

OK, now run the following command to start a TomEE 9 server and deploy the applications.

```bash
mvn clean package tomee:run -Ptomee
```

You will see the following  info in the TomEE console.

```bash
[INFO] --- tomee-maven-plugin:8.0.5:run (default-cli) @ jakartaee9-starter-boilerplate ---
[INFO] TomEE was unzipped in 'D:\hantsylabs\jakartaee9-starter-boilerplate\target\apache-tomee'
[INFO] Removed not mandatory default webapps
[INFO] Installed 'D:\hantsylabs\jakartaee9-starter-boilerplate\target\jakartaee9-starter-boilerplate.war' in D:\hantsylabs\jakartaee9-starter-boilerplate\target\apache-tomee\webapps\ja
kartaee9-starter-boilerplate.war
[INFO] TomEE will run in development mode
[INFO] Running 'org.apache.openejb.maven.plugin.run'. Configured TomEE in plugin is localhost:8080 (plugin shutdown port is 8005 and https port is null)
// ...
29-Nov-2020 14:50:20.179 INFO [main] jdk.internal.reflect.NativeMethodAccessorImpl.invoke Server version name:   Apache Tomcat (TomEE)/9.0.39 (8.0.5)
29-Nov-2020 14:50:20.179 INFO [main] jdk.internal.reflect.NativeMethodAccessorImpl.invoke Server built:          Oct 6 2020 14:11:46 UTC
29-Nov-2020 14:50:20.179 INFO [main] jdk.internal.reflect.NativeMethodAccessorImpl.invoke Server version number: 9.0.39.0
29-Nov-2020 14:50:20.179 INFO [main] jdk.internal.reflect.NativeMethodAccessorImpl.invoke OS Name:               Windows 10
29-Nov-2020 14:50:20.179 INFO [main] jdk.internal.reflect.NativeMethodAccessorImpl.invoke OS Version:            10.0
29-Nov-2020 14:50:20.179 INFO [main] jdk.internal.reflect.NativeMethodAccessorImpl.invoke Architecture:          amd64
29-Nov-2020 14:50:20.179 INFO [main] jdk.internal.reflect.NativeMethodAccessorImpl.invoke Java Home:             D:\jdk11
29-Nov-2020 14:50:20.194 INFO [main] jdk.internal.reflect.NativeMethodAccessorImpl.invoke JVM Version:           11.0.9.1+1
29-Nov-2020 14:50:20.194 INFO [main] jdk.internal.reflect.NativeMethodAccessorImpl.invoke JVM Vendor:            AdoptOpenJDK
29-Nov-2020 14:50:20.194 INFO [main] jdk.internal.reflect.NativeMethodAccessorImpl.invoke CATALINA_BASE:         D:\hantsylabs\jakartaee9-starter-boilerplate\target\apache-tomee
29-Nov-2020 14:50:20.194 INFO [main] jdk.internal.reflect.NativeMethodAccessorImpl.invoke CATALINA_HOME:         D:\hantsylabs\jakartaee9-starter-boilerplate\target\apache-tomee

//...

29-Nov-2020 14:50:28.371 INFO [main] org.apache.openejb.server.cxf.rs.CxfRsHttpListener.logEndpoints REST Application: http://localhost:8080/jakartaee9-starter-boilerplate/api
        -> com.example.JaxrsActivator@2ab8589a
29-Nov-2020 14:50:28.386 INFO [main] org.apache.openejb.server.cxf.rs.CxfRsHttpListener.logEndpoints      Service URI: http://localhost:8080/jakartaee9-starter-boilerplate/api/greeting
        -> Pojo com.example.GreetingResource
29-Nov-2020 14:50:28.386 INFO [main] org.apache.openejb.server.cxf.rs.CxfRsHttpListener.logEndpoints               GET http://localhost:8080/jakartaee9-starter-boilerplate/api/greeting
/{name} ->      Response greeting(String)
...
29-Nov-2020 14:50:29.325 WARNING [main] org.apache.catalina.util.SessionIdGeneratorBase.createSecureRandom Creation of SecureRandom instance for session ID generation using [SHA1PRNG]
took [829] milliseconds.
29-Nov-2020 14:50:29.341 INFO [main] jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke Starting ProtocolHandler ["http-nio-8080"]
29-Nov-2020 14:50:29.348 INFO [main] jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke Server startup in [6177] milliseconds

```

You can also use `tomee-maven-plugin` to deploy the application to a running TomEE server, esp. running on a remote server.

Open the  *[tomee install dir]/conf/tomcat-users.xml* file, uncomment the following the configuration.

```xml
<!-- Activate those lines to get access to TomEE GUI if added (tomee-webaccess) -->

<role rolename="tomee-admin" />
<user username="tomee" password="tomee" roles="tomee-admin,manager-gui" />

```

Open  the  *[tomee install dir]/conf/system.properties* file, uncomment and modify the following items.

```properties
tomee.remote.support = true
tomee.serialization.class.blacklist = -
tomee.serialization.class.whitelist = com.example.
openejb.system.apps = true
openejb.servicemanager.enabled = true
```

Let's return to the project *pom.xml* file,  replace the former `tomee-maven-plugin` configuration with the following.

```xml
<configuration>
    <tomeeHost>localhost</tomeeHost>
    <user>tomee</user>
    <password>tomee</password>
    <path>${project.build.directory}/${project.build.finalName}.war</path>
</configuration>
```

Make sure the TomEE server is running, run the following command in the project root folder.

```bash
mvn clean package tomee:deploy -Ptomee
```

You can view the deployment progress info in the TomEE console.

```bash
...
29-Nov-2020 16:56:23.550 INFO [http-nio-8080-exec-8] org.apache.openejb.server.cxf.rs.CxfRsHttpListener.logEndpoints REST Application: http://localhost:8080/jakartaee9-starter-boilerplate/api                 -> com.example.JaxrsActivator@affb16f
29-Nov-2020 16:56:23.550 INFO [http-nio-8080-exec-8] org.apache.openejb.server.cxf.rs.CxfRsHttpListener.logEndpoints      Service URI: http://localhost:8080/jakartaee9-starter-boilerplate/api/greeting        -> Pojo com.example.GreetingResource
29-Nov-2020 16:56:23.550 INFO [http-nio-8080-exec-8] org.apache.openejb.server.cxf.rs.CxfRsHttpListener.logEndpoints               GET http://localhost:8080/jakartaee9-starter-boilerplate/api/greeting/{name} ->      Response greeting(String)
```

To undeploy the application, execute the following command.

```bash
mvn tomee:undeploy -Ptomee
```

