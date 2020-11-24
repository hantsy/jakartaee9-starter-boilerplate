# Deploying Jakarta EE 9 applications to WildFly 

[WildFly 22.0.0.Alph1](https://www.wildfly.org/) provides a standalone preview distribution for Jakarta EE 9, go to the [Download](https://www.wildfly.org/downloads/) page, and make sure you are downloading the [Jakarta EE 9 preview version](https://download.jboss.org/wildfly/22.0.0.Alpha1/wildfly-preview-22.0.0.Alpha1.zip).

## Prerequisites 

Make sure you have installed the following software.
* Java 8 or Java 11 
* Apache Maven 3.6
* WildFly 22.0.0.Alpha1 Jakarta EE 9 preview

## Manual deployment

Clone the [source codes](https://github.com/hantsy/jakartaee9-starter-boilerplate) from my github account, and  then build the project.

```bash
mvn clean package
```

When it is done, there is a *jakartaee9-starter-boilerplate.war* file packaged in the *target* folder.

Extract the WildFly files into your local disk, enter the WildFly folder, and start the WildFly server .

```bash
# cd [wildfly-preview-22.0.0.Alpha1 install dir]\bin

# standalone.bat
Calling "D:\appsvr\wildfly-preview-22.0.0.Alpha1\bin\standalone.conf.bat"
Setting JAVA property to "D:\jdk11\bin\java"
===============================================================================

  JBoss Bootstrap Environment

  JBOSS_HOME: "D:\appsvr\wildfly-preview-22.0.0.Alpha1"

  JAVA: "D:\jdk11\bin\java"

  JAVA_OPTS: "-client -Dprogram.name=standalone.bat -Xms64M -Xmx512M -XX:MetaspaceSize=96M -XX:MaxMetaspaceSize=256m -Djava.net.preferIPv4Stack=true -Djboss.modules.system.pkgs=org.jboss.byteman  --add-exports=java.base/sun.nio.ch=ALL-UNNAMED --add-exports=jdk.unsupported/sun.misc=ALL-UNNAMED --add-exports=jdk.unsupported/sun.reflect=ALL-UNNAMED"

===============================================================================
  ...
  14:06:11,863 INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0051: Admin console listening on http://127.0.0.1:9990
```

Here I executed the traditional batch command in Windows CMD. Please use `standalone.ps1` instead when you are using a Windows PowerShell, or `standalone.sh` when using a Linux like terminal.

To deploy our application,  just copy the war to the *wildfly-preview-22.0.0.Alpha1 dir/standalone/deployments* folder.

It will perform an deployment automatically,  and generates a `.deployed`  file to indicate the deployment status is accomplished. 

To undeploy the application,  just remove the war file from  the *wildfly-preview-22.0.0.Alpha1 dir/standalone/deployments* folder. It will generate a `.undeployed` file to indicate the undeploy action is done successfully.

## Using WildFly maven plugin

Declare a `wildfly-maven-plugin` configuration under `build/plugins` section in the project *pom.xml* file.

```xml
 <plugin>
     <groupId>org.wildfly.plugins</groupId>
     <artifactId>wildfly-maven-plugin</artifactId>
     <version>${wildfly-maven-plugin.version}</version>
 </plugin>
```
Simply run the following command to deploy the application to WildFly server.

```bash
mvn clean package wildfly:run -Dwildfly.version=22.0.0.Alpha1 -Dwildfly.artifactId=wildfly-preview-dist
```
> NOTE:  You have to specify `wildfly-preview-dist` as artifact id to pick up the Jakarta EE 9 preview version. If it is not set, it will choose `wildfly-dist` which is Jakarta EE 8 compatible.

The `wildfly-maven-plugin` manages lifecycle of the WildFly server here, it will try to download and prepare a  copy of WildFly server for this application, and start it, and then deploy the application to the WildFly server.

If you wan to reuse the existing WildFly server in your machine, configure a `jboss-as.home` or `jbossHome` property in the plugin's `configuration` section.

```xml
<configuration>
    <!-- if a jboss-as.home or jboss.home property is not present, firstly it will download
                            a copy of wildfly distribution automatically -->
    <!-- <jossHome></jbossHome>-->
    ...
```

Or append a `jboss-as.home` parameter to run the `wildfly:run` goal.

```bash
mvn clean package wildfly:run -Djboss-as.home=[ the path of wildfly-preview-22.0.0.Alpha1 install dir]
```

If you wan to  deploy the application to a running WildFly server, esp the WildFly server is located in a remote host. Configure `hostname`, `port`, and admin account info of the WildFly server in the configuration.

```xml
<configuration>

    <!-- To deploy a running wildfly server -->
    <hostname></hostname>
    <port></port>
    <username></username>
    <password></password>
    ...
```

Using the following command to deploy and undeploy the application.

```bash
# deploy applications
mvn clean package wildfly:deploy

# undeploy 
mvn wildfly:undeploy
```

In the server log of the running WildFly server, you can see the deploy and undeploy progress like the following.

```bash
15:07:20,568 INFO  [org.jboss.as.repository] (management-handler-thread - 1) WFLYDR0001: Content added at location D:\appsvr\wildfly-preview-22.0.0.Alpha1\standalone\data\content\9b\8908496997cd79b1bfa229ef2a24107315b429\content
15:07:20,568 INFO  [org.jboss.as.server.deployment] (MSC service thread 1-5) WFLYSRV0027: Starting deployment of "jakartaee9-starter-boilerplate.war" (runtime-name: "jakartaee9-starter-boilerplate.war")
15:07:25,093 INFO  [org.jboss.weld.deployer] (MSC service thread 1-4) WFLYWELD0003: Processing weld deployment jakartaee9-starter-boilerplate.war
15:07:25,165 INFO  [io.jaegertracing.internal.JaegerTracer] (MSC service thread 1-4) No shutdown hook registered: Please call close() manually on application shutdown.
15:07:25,250 INFO  [io.smallrye.metrics] (MSC service thread 1-6) MicroProfile: Metrics activated (SmallRye Metrics version: 2.4.4)
15:07:25,438 INFO  [org.jboss.resteasy.resteasy_jaxrs.i18n] (ServerService Thread Pool -- 87) RESTEASY002225: Deploying jakarta.ws.rs.core.Application: class com.example.JaxrsActivator$Proxy$_$$_WeldClientProxy
15:07:25,438 INFO  [org.wildfly.extension.undertow] (ServerService Thread Pool -- 87) WFLYUT0021: Registered web context: '/jakartaee9-starter-boilerplate' for server 'default-server'
15:07:25,469 INFO  [org.jboss.as.server] (management-handler-thread - 1) WFLYSRV0010: Deployed "jakartaee9-starter-boilerplate.war" (runtime-name : "jakartaee9-starter-boilerplate.war")
15:07:36,538 INFO  [org.wildfly.extension.undertow] (ServerService Thread Pool -- 87) WFLYUT0022: Unregistered web context: '/jakartaee9-starter-boilerplate' from server 'default-server'

// starting a undeployment
15:07:36,569 INFO  [org.jboss.as.server.deployment] (MSC service thread 1-1) WFLYSRV0028: Stopped deployment jakartaee9-starter-boilerplate.war (runtime-name: jakartaee9-starter-boilerplate.war) in 47ms
15:07:36,616 INFO  [org.jboss.as.repository] (management-handler-thread - 1) WFLYDR0002: Content removed from location D:\appsvr\wildfly-preview-22.0.0.Alpha1\standalone\data\content\9b\8908496997cd79b1bfa229ef2a24107315b429\content
15:07:36,616 INFO  [org.jboss.as.server] (management-handler-thread - 1) WFLYSRV0009: Undeployed "jakartaee9-starter-boilerplate.war" (runtime-name: "jakartaee9-starter-boilerplate.war")
```

For security consideration, you can configure these sensitive info, such as `username` , `password` , etc.  under the  `server` section  in your local Maven settings(*~/.m2/settings.xml*), and refer it by id in the configuration.

```xml
<configuration>
    <!-- or set a wildfly.id property to add a server in settings.xml -->
    <!--<id>wildfly-svr</id> -->
</configuration>
```





