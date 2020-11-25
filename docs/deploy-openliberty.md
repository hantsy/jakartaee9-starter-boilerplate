# Deploying Jakarta EE 9 applications to Open Liberty

[Open Liberty](https://openliberty.io) has provided  a standalone preview distribution for Jakarta EE 9 in the past development iterations.  At the moment I wrote this post, the latest version is 21.0.0.1-beta. Open Liberty follows a monthly publication cycle, I think the stale version will be provided in the next months.

Go to the [Download](https://openliberty.io/downloads) page and switch to *Beta* tab,  there are two package options in the table.  
*  Jakarta EE 9 Beta Features
*  All All Beta Features

And make sure you are downloading the [Jakarta EE 9 Beta Features](https://openliberty.io/downloads/#runtime_betas).

## Prerequisites 

Make sure you have installed the following software.
* Java 8 or Java 11 
* Apache Maven 3.6
* Open Liberty 21.0.0.1-beta

## Deploying to Open Liberty manually

Clone the [source codes](https://github.com/hantsy/jakartaee9-starter-boilerplate) from my github account, and  then build the project.

```bash
mvn clean package
```

When it is done, there is a *jakartaee9-starter-boilerplate.war* file packaged in the *target* folder.

Enter the Open Liberty folder, start up the Open Liberty server.

```bash
# cd wlp\bin

# server start // `server run` to show logs in the fontend console
Starting server defaultServer.
CWWKE0953W: This version of Open Liberty is an unsupported early release version.
Server defaultServer started.
```

If you are the first time to run server,  it will create a *server profile* for you. It will create a new folder named *defaultServer* in the *usr/servers* folder  to prepare all resources of a `jakartaee-9.0` feature pack, check the *usr/servers/defaultServer/server.xml* file for details .

> NOTE:  The server profile is similar to the domain concept in other application servers, such as Glassfish, WildFly, etc.

To deploy our application,  just copy the war to the *wlp/usr/servers/defaultServer/dropins* folder.

In the *usr/servers/defaultServer/logs/messages.log* file, you will see the deployment progress.

```bash
[11/24/20, 16:00:07:304 CST] 00000046 com.ibm.ws.app.manager.AppMessageHelper                      I CWWKZ0018I: Starting application jakartaee9-starter-boilerplate.
[11/24/20, 16:00:07:304 CST] 00000046 bm.ws.app.manager.war.internal.WARDeployedAppInfoFactoryImpl I CWWKZ0133I: The jakartaee9-starter-boilerplate application at the D:\appsvr\wlp\usr\servers\defaultServer\dropins\jakartaee9-starter-boilerplate.war location is being expanded to the D:\appsvr\wlp\usr\servers\defaultServer\apps\expanded\jakartaee9-starter-boilerplate.war directory.
[11/24/20, 16:00:09:629 CST] 00000046 org.jboss.weld.Version                                       I WELD-000900: 4.0.0 (Alpha3)
[11/24/20, 16:00:10:709 CST] 00000046 org.jboss.weld.Event                                         I WELD-000411: Observer method [BackedAnnotatedMethod] org.apache.myfaces.config.annotation.CdiAnnotationProviderExtension.processAnnotatedType(@Observes ProcessAnnotatedType<T>) receives events for all annotated types. Consider restricting events using @WithAnnotations or a generic type with bounds.
[11/24/20, 16:00:10:726 CST] 00000046 org.jboss.weld.Event                                         I WELD-000411: Observer method [BackedAnnotatedMethod] org.apache.myfaces.cdi.JsfArtifactProducerExtension.processAnnotatedType(@Observes ProcessAnnotatedType<T>, BeanManager) receives events for all annotated types. Consider restricting events using @WithAnnotations or a generic type with bounds.
[11/24/20, 16:00:11:770 CST] 00000046 com.ibm.ws.webcontainer.osgi.webapp.WebGroup                 I SRVE0169I: Loading Web Module: jakartaee9-starter-boilerplate.
[11/24/20, 16:00:11:786 CST] 00000046 com.ibm.ws.webcontainer                                      I SRVE0250I: Web Module jakartaee9-starter-boilerplate has been bound to default_host.
[11/24/20, 16:00:11:786 CST] 00000046 com.ibm.ws.http.internal.VirtualHostImpl                     A CWWKT0016I: Web application available (default_host): http://localhost:9080/jakartaee9-starter-boilerplate/
[11/24/20, 16:00:11:786 CST] 0000004e com.ibm.ws.session.WASSessionCore                            I SESN0176I: A new session context will be created for application key default_host/jakartaee9-starter-boilerplate
[11/24/20, 16:00:11:786 CST] 0000004e com.ibm.ws.util                                              I SESN0172I: The session manager is using the Java default SecureRandom implementation for session ID generation.
[11/24/20, 16:00:11:864 CST] 00000046 com.ibm.ws.webcontainer.osgi.mbeans.PluginGenerator          I SRVE9103I: A configuration file for a web server plugin was automatically generated for this server at D:\appsvr\wlp\usr\servers\defaultServer\logs\state\plugin-cfg.xml.
[11/24/20, 16:00:11:896 CST] 0000004e org.apache.myfaces.ee.MyFacesContainerInitializer            I Using org.apache.myfaces.ee.MyFacesContainerInitializer
[11/24/20, 16:00:12:005 CST] 0000004e com.ibm.ws.app.manager.AppMessageHelper                      A CWWKZ0001I: Application jakartaee9-starter-boilerplate started in 4.701 seconds.
[11/24/20, 16:00:12:370 CST] 0000004e org.jboss.resteasy.resteasy_jaxrs.i18n                       I RESTEASY002225: Deploying jakarta.ws.rs.core.Application: class com.example.JaxrsActivator$Proxy$_$$_WeldClientProxy
[11/24/20, 16:00:12:433 CST] 0000004e com.ibm.ws.webcontainer.servlet                              I SRVE0242I: [jakartaee9-starter-boilerplate] [/jakartaee9-starter-boilerplate] [com.example.JaxrsActivator]: Initialization successful.
```

To undeploy the application,  just remove the war file from  the *wlp/usr/servers/defaultServer/dropins* folder.

Check  the *usr/servers/defaultServer/logs/messages.log* file, you will see the log like this.

```bash
[11/24/20, 16:04:13:245 CST] 00000066 com.ibm.ws.http.internal.VirtualHostImpl                     A CWWKT0017I: Web application removed (default_host): http://localhost:9080/jakartaee9-starter-boilerplate/
[11/24/20, 16:04:13:248 CST] 00000066 com.ibm.ws.webcontainer.servlet                              I SRVE0253I: [jakartaee9-starter-boilerplate] [/jakartaee9-starter-boilerplate] [com.example.JaxrsActivator]: Destroy successful.
[11/24/20, 16:04:13:281 CST] 00000066 com.ibm.ws.app.manager.AppMessageHelper                      A CWWKZ0009I: The application jakartaee9-starter-boilerplate has stopped successfully.
[11/24/20, 16:04:13:359 CST] 00000027 com.ibm.ws.webcontainer.osgi.mbeans.PluginGenerator          I SRVE9103I: A configuration file for a web server plugin was automatically generated for this server at D:\appsvr\wlp\usr\servers\defaultServer\logs\state\plugin-cfg.xml.
```



## Using Liberty Maven Plugin

Declare a `liberty-maven-plugin` configuration under `build/plugins` section in the project *pom.xml* file.

```xml
<plugin>
    <groupId>io.openliberty.tools</groupId>
    <artifactId>liberty-maven-plugin</artifactId>
    <version>${liberty-maven-plugin.version}</version>
</plugin>
```
When simply running `liberty:run` , it will retrieve the latest `io.openliberty:openliberty-kernel` and install required features defined in your project specific `src/main/liberty/config/server.xml`  file, then start the server and deploy your application to this server.

To use the latest Open Liberty Jakarta EE 9 beta feature pack to run our application, configure the `runtimeArtifact` in the `liberty-maven-plugin` configuration to replace the default  `openlibety-kernel`.

```xml
<runtimeArtifact>
    <groupId>io.openliberty.beta</groupId>
    <artifactId>openliberty-jakartaee9</artifactId>
    <version>${liberty.runtime.version}</version>
</runtimeArtifact>
```

The  `liberty.runtime.version` property is defined in the `properties` section.

```xml
<liberty.runtime.version>21.0.0.1-beta</liberty.runtime.version>
```

Run the following command to deploy our application to the Open Liberty server.

```bash
mvn clean liberty:run
```

You will see the following message in the console.

```bash
[INFO] --- liberty-maven-plugin:3.3.1:run (default-cli) @ jakartaee9-starter-boilerplate ---
[INFO] The runtimeArtifact version 21.0.0.1-beta is overwritten by the liberty.runtime.version value 21.0.0.1-beta.
[INFO] CWWKM2102I: Using artifact based assembly archive : io.openliberty.beta:openliberty-jakartaee9:null:21.0.0.1-beta:zip.
[INFO] CWWKM2102I: Using installDirectory : D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp.
[INFO] CWWKM2102I: Using serverName : defaultServer.
[INFO] CWWKM2102I: Using serverDirectory : D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp\usr\servers\defaultServer.
[INFO] Running maven-compiler-plugin:compile
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 4 source files to D:\hantsylabs\jakartaee9-starter-boilerplate\target\classes
[INFO] Running maven-resources-plugin:resources
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Copying 1 resource
[INFO] Running liberty:create
[INFO] The runtimeArtifact version 21.0.0.1-beta is overwritten by the liberty.runtime.version value 21.0.0.1-beta.
[INFO] CWWKM2102I: Using artifact based assembly archive : io.openliberty.beta:openliberty-jakartaee9:null:21.0.0.1-beta:zip.
[INFO] CWWKM2102I: Using installDirectory : D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp.
[INFO] CWWKM2102I: Using serverName : defaultServer.
[INFO] CWWKM2102I: Using serverDirectory : D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp\usr\servers\defaultServer.
[INFO] Installing assembly...
[INFO] Expanding: C:\Users\hantsy\.m2\repository\io\openliberty\beta\openliberty-jakartaee9\21.0.0.1-beta\openliberty-jakartaee9-21.0.0.1-beta.zip into D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty
[INFO] CWWKM2143I: Server defaultServer does not exist. Now creating...
[INFO] CWWKM2001I: Invoke command is ["D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp\bin\server.bat", create, defaultServer].
[INFO] Server defaultServer created.
[INFO] CWWKM2129I: Server defaultServer has been created at D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp\usr\servers\defaultServer.
[INFO] Copying 1 file to D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp\usr\servers\defaultServer
[INFO] CWWKM2144I: Update server configuration file server.xml from D:\hantsylabs\jakartaee9-starter-boilerplate\src\main\liberty\config\server.xml.
[INFO] Running liberty:install-feature
[INFO] The runtimeArtifact version 21.0.0.1-beta is overwritten by the liberty.runtime.version value 21.0.0.1-beta.
[INFO] CWWKM2102I: Using artifact based assembly archive : io.openliberty.beta:openliberty-jakartaee9:null:21.0.0.1-beta:zip.
[INFO] CWWKM2102I: Using installDirectory : D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp.
[INFO] CWWKM2102I: Using serverName : defaultServer.
[INFO] CWWKM2102I: Using serverDirectory : D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp\usr\servers\defaultServer.
[WARNING] Features that are not included with the beta runtime cannot be installed. Features that are included with the beta runtime can be enabled by adding them to your server.xml file.
[INFO] Running liberty:deploy
[INFO] The runtimeArtifact version 21.0.0.1-beta is overwritten by the liberty.runtime.version value 21.0.0.1-beta.
[INFO] CWWKM2102I: Using artifact based assembly archive : io.openliberty.beta:openliberty-jakartaee9:null:21.0.0.1-beta:zip.
[INFO] CWWKM2102I: Using installDirectory : D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp.
[INFO] CWWKM2102I: Using serverName : defaultServer.
[INFO] CWWKM2102I: Using serverDirectory : D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp\usr\servers\defaultServer.
[INFO] Copying 1 file to D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp\usr\servers\defaultServer
[INFO] CWWKM2144I: Update server configuration file server.xml from D:\hantsylabs\jakartaee9-starter-boilerplate\src\main\liberty\config\server.xml.
[INFO] CWWKM2185I: The liberty-maven-plugin configuration parameter "appsDirectory" value defaults to "dropins".
[INFO] CWWKM2160I: Installing application jakartaee9-starter-boilerplate.war.xml.
[INFO] Copying 1 file to D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp\usr\servers\defaultServer
[INFO] CWWKM2144I: Update server configuration file server.xml from D:\hantsylabs\jakartaee9-starter-boilerplate\src\main\liberty\config\server.xml.
[INFO] CWWKM2001I: Invoke command is ["D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp\bin\server.bat", run, defaultServer].
[INFO] Launching defaultServer (Open Liberty 21.0.0.1-beta/wlp-1.0.47.cl201220201111-0736) on OpenJDK 64-Bit Server VM, version 11.0.7+10 (en_US)
[INFO] CWWKE0953W: This version of Open Liberty is an unsupported early release version.
[INFO] [AUDIT   ] CWWKE0001I: The server defaultServer has been launched.
[INFO] [WARNING ] CWWKS3103W: There are no users defined for the BasicRegistry configuration of ID com.ibm.ws.security.registry.basic.config[basic].
[INFO] [AUDIT   ] CWWKZ0058I: Monitoring dropins for applications.
[INFO] [AUDIT   ] CWPKI0820A: The default keystore has been created using the 'keystore_password' environment variable.
[INFO] [AUDIT   ] CWWKS4104A: LTPA keys created in 1.056 seconds. LTPA key file: D:/hantsylabs/jakartaee9-starter-boilerplate/target/liberty/wlp/usr/servers/defaultServer/resources/security/ltpa.keys
[INFO] [AUDIT   ] CWWKT0016I: Web application available (default_host): http://localhost:9080/ibm/api/
[INFO] [AUDIT   ] CWWKT0016I: Web application available (default_host): http://localhost:9080/IBMJMXConnectorREST/
[INFO] [AUDIT   ] CWWKT0016I: Web application available (default_host): http://localhost:9080/jakartaee9-starter-boilerplate/
[INFO] [AUDIT   ] CWWKZ0001I: Application jakartaee9-starter-boilerplate started in 6.360 seconds.
[INFO] [AUDIT   ] CWWKF0012I: The server installed the following features: [appClientSupport-2.0, appSecurity-4.0, beanValidation-3.0, cdi-3.0, concurrent-2.0, connectors-2.0, connectorsInboundSecurity-2.0, distributedMap-1.0, enterpriseBeans-4.0, enterpriseBeansHome-4.0, enterpriseBeansLite-4.0, enterpriseBeansPersistentTimer-4.0, enterpriseBeansRemote-4.0, expressionLanguage-4.0, faces-3.0, jacc-2.0, jakartaee-9.0, jaspic-2.0, jaxb-3.0, jdbc-4.2, jndi-1.0, json-1.0, jsonb-2.0, jsonp-2.0, mail-2.0, managedBeans-2.0, mdb-4.0, messaging-3.0, messagingClient-3.0, messagingSecurity-3.0, messagingServer-3.0, pages-3.0, persistence-3.0, persistenceContainer-3.0, restConnector-2.0, restfulWS-3.0, restfulWSClient-3.0, servlet-5.0, ssl-1.0, webProfile-9.0, websocket-2.0].
[INFO] [AUDIT   ] CWWKF0011I: The defaultServer server is ready to run a smarter planet. The defaultServer server started in 52.202 seconds.
[INFO] [AUDIT   ] CWPKI0803A: SSL certificate created in 8.997 seconds. SSL key file: D:/hantsylabs/jakartaee9-starter-boilerplate/target/liberty/wlp/usr/servers/defaultServer/resources/security/key.p12
[INFO] [AUDIT   ] CWWKI0001I: The CORBA name server is now available at corbaloc:iiop:localhost:2809/NameService.
```

> Note: The beta feature pack use a different **groupId** (`io.openliberty.beta `)in the Maven archetype.

To undeploy the applicaiton and stop the server, just send a `CTRL+C` to the console, you will see the following info.

```bash
[INFO] [AUDIT   ] CWWKE1100I: Waiting for up to 30 seconds for the server to quiesce.
[INFO] [AUDIT   ] CWWKT0017I: Web application removed (default_host): http://localhost:9080/jakartaee9-starter-boilerplate/
[INFO] [AUDIT   ] CWWKT0017I: Web application removed (default_host): http://localhost:9080/ibm/api/
[INFO] [AUDIT   ] CWWKT0017I: Web application removed (default_host): http://localhost:9080/IBMJMXConnectorREST/
[INFO] [AUDIT   ] CWWKZ0009I: The application jakartaee9-starter-boilerplate has stopped successfully.
[INFO] [AUDIT   ] CWWKI0002I: The CORBA name server is no longer available at corbaloc:iiop:localhost:2809/NameService.
```

You can also specify a  `installDirectory` property in the plugin *configuration* to use an existing Open Liberty server. 

In a CI server , to prepare an Open Liberty server from scratch, add a `dependency:unpack`  to retrieve the Open Liberty archive from Maven Central directly.

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <version>${maven-dependency-plugin.version}</version>
    <executions>
        <execution>
            <id>default-unpack</id>
            <phase>process-resources</phase>
            <goals>
                <goal>unpack</goal>
            </goals>
            <configuration>
                <artifactItems>
                    <artifactItem>
                        <groupId>io.openliberty.beta</groupId>
                        <artifactId>openliberty-jakartaee9</artifactId>
                        <version>${liberty.runtime.version}</version>
                        <type>zip</type>
                        <overWrite>false</overWrite>
                        <outputDirectory>${project.build.directory}/liberty
                        </outputDirectory>
                    </artifactItem>
                </artifactItems>
            </configuration>
        </execution>
    </executions>
</plugin>
<!-- Enable liberty-maven-plugin -->
<plugin>
    <groupId>io.openliberty.tools</groupId>
    <artifactId>liberty-maven-plugin</artifactId>
    <version>${liberty-maven-plugin.version}</version>
    <configuration>
        <installDirectory>${project.build.directory}/liberty/wlp</installDirectory>
    </configuration>
</plugin>
```

Now run the following command to deploy our application to Open Liberty.

```bash
mvn clean package liberty:start liberty:deploy
```
> NOTE:  We do not use `run` goal here, because it will clean the **target** folder at the initial stage, which will remove the downloaded Open Liberty dist.

You will the following message when executing `start` and `deploy` goals.

```bash
[INFO] --- liberty-maven-plugin:3.3.1:start (default-cli) @ jakartaee9-starter-boilerplate ---
[INFO] CWWKM2102I: Using pre-installed assembly : D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp.
[INFO] CWWKM2102I: Using serverName : defaultServer.
[INFO] CWWKM2102I: Using serverDirectory : D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp\usr\servers\defaultServer.
[INFO] CWWKM2107I: Installation type is pre-existing; skipping installation.
[INFO] Copying 1 file to D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp\usr\servers\defaultServer
[INFO] CWWKM2144I: Update server configuration file server.xml from D:\hantsylabs\jakartaee9-starter-boilerplate\src\main\liberty\config\server.xml.
[INFO] CWWKM2001I: Invoke command is ["D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp\bin\server.bat", start, defaultServer].
[INFO] Starting server defaultServer.
[INFO] CWWKE0953W: This version of Open Liberty is an unsupported early release version.
[INFO] Server defaultServer started.
[INFO] Waiting up to 30 seconds for server confirmation:  CWWKF0011I to be found in D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp\usr\servers\defaultServer\logs\messages.log
[INFO] CWWKM2010I: Searching for CWWKF0011I in D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp\usr\servers\defaultServer\logs\messages.log. This search will timeout after 30 seconds.
[INFO] CWWKM2015I: Match number: 1 is [11/24/20, 17:38:08:783 CST] 00000022 com.ibm.ws.kernel.feature.internal.FeatureManager            A CWWKF0011I: The defaultServer server is ready to run a smarter planet. The defaultServer server started in 53.584 seconds..
[INFO]
[INFO] --- liberty-maven-plugin:3.3.1:deploy (default-cli) @ jakartaee9-starter-boilerplate ---
[INFO] CWWKM2102I: Using pre-installed assembly : D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp.
[INFO] CWWKM2102I: Using serverName : defaultServer.
[INFO] CWWKM2102I: Using serverDirectory : D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp\usr\servers\defaultServer.
[INFO] Copying 1 file to D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp\usr\servers\defaultServer
[INFO] CWWKM2144I: Update server configuration file server.xml from D:\hantsylabs\jakartaee9-starter-boilerplate\src\main\liberty\config\server.xml.
[INFO] CWWKM2185I: The liberty-maven-plugin configuration parameter "appsDirectory" value defaults to "dropins".
[INFO] CWWKM2160I: Installing application jakartaee9-starter-boilerplate.war.xml.
[INFO] CWWKM2010I: Searching for CWWKZ0001I.*jakartaee9-starter-boilerplate in D:\hantsylabs\jakartaee9-starter-boilerplate\target\liberty\wlp\usr\servers\defaultServer\logs\messages.log. This search will timeout after 40 seconds.
[INFO] CWWKM2015I: Match number: 1 is [11/24/20, 17:38:16:858 CST] 00000028 com.ibm.ws.app.manager.AppMessageHelper                      A CWWKZ0001I: Application jakartaee9-starter-boilerplate started in 4.990 seconds..
```

In the startup stage, it will update the Open Liberty server config with the one in your project if it is existed.  An example *server.xml* file is like this.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<server description="new server">

    <!-- Enable features -->
    <featureManager>
        <feature>jakartaee-9.0</feature>
    </featureManager>

    <!-- This template enables security. To get the full use of all the capabilities, a keystore and user registry are required. -->
    
    <!-- For the keystore, default keys are generated and stored in a keystore. To provide the keystore password, generate an 
         encoded password using bin/securityUtility encode and add it below in the password attribute of the keyStore element. 
         Then uncomment the keyStore element. -->
    <!--
    <keyStore password=""/> 
    -->
    
    <!--For a user registry configuration, configure your user registry. For example, configure a basic user registry using the
        basicRegistry element. Specify your own user name below in the name attribute of the user element. For the password, 
        generate an encoded password using bin/securityUtility encode and add it in the password attribute of the user element. 
        Then uncomment the user element. -->
    <basicRegistry id="basic" realm="BasicRealm"> 
        <!-- <user name="yourUserName" password="" />  --> 
    </basicRegistry>
    
    <!-- To access this server from a remote client add a host attribute to the following element, e.g. host="*" -->
    <httpEndpoint id="defaultHttpEndpoint"
                  httpPort="9080"
                  httpsPort="9443" />
                  
    <!-- Automatically expand WAR files and EAR files -->
    <applicationManager autoExpand="true"/>

</server>
```

To undeploy and stop the Open Liberty server, execute the following command .

```bash
mvn liberty:undeploy 
mvn liberty:stop 
```

You can also configure  the `installDirectory` property to use the location of your local Open Liberty server.

```xml
 <configuration>
     <installDirectory>D:/appsvr/wlp</installDirectory>
     ...
</configuration>
```

The `liberty-maven-plugin` does not support to deploy applications to a running server, see [OpenLiberty/ci.maven#16](https://github.com/OpenLiberty/ci.maven/issues/16) for details.

