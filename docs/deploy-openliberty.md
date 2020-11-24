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

# server start // server run to show logs in the fontend console
Starting server defaultServer.
CWWKE0953W: This version of Open Liberty is an unsupported early release version.
Server defaultServer started.
```

If you are the first time to run the server,  it will create a folder *defaultServer* in the *usr/servers*  to prepare all resources of a `jakartaee-9.0` feature pack, check the *usr/servers/defaultServer/server.xml* file for details .

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
When simply run `liberty:run` , it will retrieve the latest `io.openliberty:openliberty-kernel` and install required features defined in your project specific `src/main/liberty/config/server.xml`  file.

> The beta feature pack use a different groupId (`io.openliberty.beta `)in the Maven archetype.

To use the Jakarta EE 9 beta features,  add a `dependency:unpack`  to retrieve the archive directly, and then configure a `installDirectory` to allow `liberty-maven-plugin` to find the existing Open Liberty  server quickly.

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
> NOTE:  We do not use `run` goal here, because it will clean the target folder at the initial stage, which will remove the downloaded Open Liberty dist.

You can also configure  the `installDirectory` property to your location of your local Open Liberty server.

The `liberty-maven-plugin` does not support to deploy applications to a running server, see [OpenLiberty/ci.maven#16](https://github.com/OpenLiberty/ci.maven/issues/16) for details.

