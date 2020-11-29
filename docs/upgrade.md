# Upgrading to Jakarta EE 9

In the [Jakarta EE 9 Milestone Release Party](https://www.crowdcast.io/e/JakartaEE9_Milestonereleaseparty?utm_source=crowdcast&utm_medium=email&utm_campaign=followers), the Jakarta team shared the latest work from Eclipse EE4j project. 

* The Jakarta EE platform API is close to be public, currently it targets 9.0.0-RC2. 
* Glassfish v6 M1 is released.
* Eclipse Transformer tooling  helps you transform the existing Jakarta EE 8 codes to Jakarta EE 9

You can also read [Jakarta  EE is taking off ](https://eclipse-foundation.blog/2020/06/23/jakarta-ee-is-taking-off/) from the official Eclipse blog to know more details about the current status of the Jakarta EE  specification and Eclipse EE4j project.

## What is new in Jakarta EE 9

No panic, there are almost no new features added in Jakarta EE 9, but it will bring breaking changes in your codes if you are upgrading from Jakarta EE/Java EE 8.

* All **javax.\*** prefix in the package names are changed to **jakarta.\***.
* All XML namespaces in the deployment descriptor files(such as *web.xml*, *beans.xml*, etc) are changed to *https://jakarta.ee/xml/ns/jakartaee* 
* The versions of all specs jump to the next main version, eg. CDI 2.0->3.0.
* Some specs are marked as pruned in Jakarta EE 9, which means they are not available in Jakarta EE ecosystem since Jakarta EE 9, such as the old XML-RPC, etc.  For more details please read the [6.1.4. Removed Jakarta Technologies](https://jakarta.ee/specifications/platform/9/platform-spec-9-SNAPSHOT.html#a2333)  section of the official Jakarta EE specifications. You should avoid these specs and use the newest replacement in your new projects.
* Some specs are marked as *Optional* in Jakarta EE 9,  such as the traditional SOAP based Web Services related specs, please check [6.1.3. Optional Jakarta Technologies](https://jakarta.ee/specifications/platform/9/platform-spec-9-SNAPSHOT.html#a2331)  section for more details. If your want to use these specs in your new projects, you have to consult the reference document of the Jakarta EE 9 provider you are using, and make sure they are supported in the new version. 
* The APIs of Jakarta EE 9 is still stick on Java 8, ~but all compatible products(implementers) should be compatible with Java 11 at runtime~ Java 11 support is postponed to Jakarta EE 9.1.

## Upgrading to Jakarta EE 9

Currently the Jakarta EE vendor are busily moving to the new Jakarta EE 9 platform. 

* Currently [Glassfish v6.0.0.M1](https://eclipse-ee4j.github.io/glassfish/download) is available for testing Jakarta EE 9.  
* WildFly 21 and Payara platform 6 promise new compatible products in the next months.  
* [Open Liberty 20.0.0.7 beta](https://openliberty.io/downloads/#runtime_betas) already provided partial web application support of Jakarta EE 9. 
* [Jetty 11.0.0-alpha0](https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-distribution/11.0.0-alpha0/) and [Apache Tomcat 10.0-M6](https://tomcat.apache.org/download-10.cgi) announced Servlet support of Jakarta EE 9 in their new milestone products.

[Eclipse transformer tooling project](https://projects.eclipse.org/projects/technology.transformer) provides some utilities (such as cli, maven plugin, etc. ) to help you to upgrade your codes in a batch mode, currently Jakarta EE 9 rules is under development.

Another cool tool - [tomcat-jakartaee-migration](https://github.com/apache/tomcat-jakartaee-migration) is  from Apache Tomcat project, which is also used to  handle the namespace changes automatically when migrating to Jakarta EE 9 platform.

Here we will use Glassfish v6.0.0.M1 as target runtime , and manually upgrade my former [Jakarta EE 8 starter](https://github.com/hantsy/jakartaee8-starter) to Jakarta EE 9 platform.

> Note :  Unfortunately, Glassfish v6.0.0.M1 still requires **Java 8** at runtime. ~Java 11 support will be added in the next milestones~ Java 11 in Glassfish is postponed to 6.1.

### Getting Glassfish v6.0.0.M1

Download [Glassfish v6.0.0.M1](https://eclipse-ee4j.github.io/glassfish/download) and extract files into your discs.

Open your terminal, and switch to the *glassfish6/bin* folder, and start up Glassfish manually by executing the following command.

```bash
asadmin start-domain domain1
```
### Clean up the Jakarta EE 8 Codes

Now let's clean up the codes of [Jakarta EE 8 starter](https://github.com/hantsy/jakartaee8-starter)  and migrate to Jakarta EE 9 step by step. 

* Get a copy of [Jakarta EE 8 starter](https://github.com/hantsy/jakartaee8-starter), and import it into your favorite IDE.
* Open *pom.xml* file, change the value of **jakartaee-api.version** property to **9.0.0-RC2**, then update your dependencies.
* Open all Java files, replace all **java.**  prefix with **jakarta.** in the `import` clauses.
* Open *src/main/resources/MATA-INF/beans.xml*, change xml namespace to **https://jakarta.ee/xml/ns/jakartaee** and *version* to **3.0**.

> Here I describe the steps in a manual way, when the Eclipse Transformer project becomes mature, maybe I will turn back and introduce it in another post.

### Running the application

At this moment when I am writing this post, you have to use Java 8 to build and run the application on Glassfish 6.0M1.

Make sure you are using Java 8 . 

```bash
>java -version
openjdk version "1.8.0_252"
OpenJDK Runtime Environment (AdoptOpenJDK)(build 1.8.0_252-b09)
OpenJDK 64-Bit Server VM (AdoptOpenJDK)(build 25.252-b09, mixed mode)
```

> We will switch to Java 11 as default JVM when Glassfish v6 is ready for Java  11, the products from other Jakarta EE providers, such as Payara Server, WildFly, Open Liberty, etc. are compatible with Java 11 for a long time.

Next, follow these steps to deploy our sample application on the running Glassfish server.

* Run the following command to package the application into a war.
  
   ```bash
   mvn clean package
   ```
* Copy *target/jakartaee9-starter-boilerplate.war* to *glassfish6/glassfish/domains/domin1/autodeploy* folder. The war archive will be tracked by Glassfish and deployed automatically. When it is done there is *jakartaee9-starter-boilerplate.war_deployed*  lock file generated in the same folder.
* Test the sample endpoints by curl command.

   ```bash
   curl http://localhost:8080/jakartaee9-starter-boilerplate/api/greeting/JakartaEE
   {"message":"Say Hello to JakartaEE at 2020-06-24T15:45:43.771"}
   ```
* To undeploy the application, just remove it from *glassfish6/glassfish/domains/domin1/autodeploy* folder, when it is done, a *jakartaee9-starter-boilerplate.war_undeployed* file is generated instead of the former *jakartaee9-starter-boilerplate.war_deployed*.
* To stop Glassfish v6, enter *glassfish6/bin*, and run `asadmin stop-domain domain1`.

## Staying informed

I will  keep updating [this sample project](https://github.com/hantsy/jakartaee9-starter-boilerplate) as the related products is coming to the Jakarta EE 9 support, eg.

* Adding WildFly,  Open Liberty, Payara Server support back
* Refreshing the Arquillian testing codes when it is aligned to Jakarta EE 9
* Updating the configuration of  cargo maven plugin and other maven plugins to manage the latest application servers.

And do not forget track the latest new of Jakarta EE from the official  [Jakarta EE](https://jakarta.ee/) website.

