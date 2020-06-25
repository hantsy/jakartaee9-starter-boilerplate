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
* Some specs are marked as pruned/deprecated, such as XML-RPC, etc.  These specs should be avoided in your new projects, they may be removed in the further releases.
* The APIs of Jakarta EE 9 is still stick on Java 8, but all compatible products(implementers) should be compatible with Java 11 at runtime.

## Upgrading to Jakarta EE 9

Currently the Jakarta EE vendor are busily moving to the new Jakarta EE 9 platform. 

* Currently [Glassfish v6.0.0.M1](https://eclipse-ee4j.github.io/glassfish/download) is available for testing Jakarta EE 9.  
* WildFly 21 and Payara platform 6 will provide compatible products in the next months.  
* [Open Liberty 20.0.0.7 beta](https://openliberty.io/downloads/#runtime_betas) provided partial web application support of Jakarta EE 9. 
* [Jetty 11.0.0-alpha0](https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-distribution/11.0.0-alpha0/) and [Apache Tomcat 10.0-M6](https://tomcat.apache.org/download-10.cgi) announced Servlet support of Jakarta EE 9 in their new milestone products.

[Eclipse transformer tooling project](https://projects.eclipse.org/projects/technology.transformer) provide utilities (cli, maven plugin ) to help your upgrade the codes in batch mode, currently Jakarta EE 9 rules is under development.

Another tool - [tomcat-jakartaee-migration](https://github.com/apache/tomcat-jakartaee-migration) is  from Apache Tomcat project, which is also used to  handle the changes of naming rules automatically when migrating to Jakarta EE 9 platform.

Here we will use Glassfish v6.0.0.M1 as target runtime , and manually upgrade my former [Jakarta EE 8 starter](https://github.com/hantsy/jakartaee8-starter) to Jakarta EE 9 platform.

> Note :  Unfortunately, Glassfish v6.0.0.M1 still requires **Java 8** at runtime. Java 11 support will be added in the next milestones.

### Getting Glassfish v6.0.0.M1

Download [Glassfish v6.0.0.M1](https://eclipse-ee4j.github.io/glassfish/download), extract files into your discs.

Open your terminal, and enter the *glassfish6/bin* folder, and start up Glassfish manually by executing the following command.

```bash
asadmin start-domain domain1
```
### Clean up the Jakarta EE 8 codes

As an example, we will use existing [Jakarta EE 8 starter](https://github.com/hantsy/jakartaee8-starter)  here. 

* Get a copy of [Jakarta EE 8 starter](https://github.com/hantsy/jakartaee8-starter), and import it into your favorite IDE.
* Open *pom.xml* file, change the value of **jakartaee-api.version** property to **9.0.0-RC2**, then update your dependencies.
* Open all Java files, replace all **java.**  prefix with **jakarta.** in the `import` clauses.
* Open *src/main/resources/MATA-INF/beans.xml*, change xml namespace to **https://jakarta.ee/xml/ns/jakartaee** and *version* to **3.0**.

> Here I described the steps of manual upgrade, when the Eclipse Transformer project becomes mature, maybe I will introduce it in another post.

### Running the application

Make sure you are using Java 8 at the moment. We will switch to Java 11 in the next milestone of the Glassfish v6.

* Run the following command to package the application into a war.
  
   ```bash
   mvn clean package
   ```
* Copy *target/jakartaee9-starter-boilerplate.war* to *glassfish6/glassfish/domains/domin1/autodeploy* folder. The war archive will be tracked by Glassfish and deploy it automatically. When it is done there is *jakartaee9-starter-boilerplate.war_deployed* generated in the same folder.
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
* Refresh the Arquillian testing codes when it is ready for Jakarta EE 9
* Updating the configuration of  cargo maven plugin and other maven plugins to manage the latest application servers.

And do not forget track the latest new of Jakarta EE from the official  [Jakarta EE](https://jakarta.ee/) website.

