# Deploying Jakarta EE 9 applications to Glassfish v6 using Cargo maven plugin

From the Jakarta EE mail subscription, I think Jakarta EE 9 is ready for the public and the official announcement will come soon in a few days.

In the former [Jakarta EE 8 starter boilerplate](https://github.com/hantsy/jakartaee8-starter) , I have described [how to deploy a Jakarta EE 8 application on Glassfish v5](https://github.com/hantsy/jakartaee8-starter/blob/master/docs/03run-glassfish-mvn.md), in this post, I will update it and try to deploy the Jakarta EE 9 sample application to a Glassfish v6 application server.

## Prerequisites

Make sure you have installed the following software.

* Java 8, yes, Glassfish v6 still only supports Java 8, the next 6.1 will focus on Java 11 support. Oracle JDK 8 or AdoptOpenJDK 8 is recommended.
* Download and install [Glassfish v6](https://github.com/eclipse-ee4j/glassfish/releases) , this is required when you are deploying the applications via the **existing** and remote **runtime** configuration. 
* Download and install [Apache Maven](http://maven.apache.org/) .
* Get to know the basic of [Cargo maven plugin](https://codehaus-cargo.github.io/). 

## Deploying the applications

Clone the [Jakarta EE9 starter boilerplate](https://github.com/hantsy/jakartaee9-starter-boilerplate) repository into your local disk.

Open a terminal and switch to the project root folder, execute the following command to build and deploy this Jakarta EE 9 application to Glassfish v6 .

```bash
mvn clean verify org.codehaus.cargo:cargo-maven3-plugin:1.9.3:run 
-Dcargo.maven.containerId=glassfish6x   
-Dcargo.maven.containerUrl=https://repo.maven.apache.org/maven2/org/glassfish/main/distributions/glassfish/6.0.0/glassfish-6.0.0.zip
-Dcargo.servlet.port=8080
```

This command will use Maven to build the project, and then call  cargo maven plugin to download a copy of Glassfish v6 from the the specified URL, and then try to prepare a new domain configuration for the application deployment, then start the new created domain, and final deploy the deployable file( `target\jakartaee9-starter-boilerplate.war`).

You will see info similar to the following when the deployment is successful.

```bash
[INFO] --- cargo-maven3-plugin:1.9.3:run (default-cli) @ jakartaee9-starter-boilerplate ---
[INFO] [en2.ContainerRunMojo] Resolved container artifact org.codehaus.cargo:cargo-core-container-glassfish:jar:1.8.2 for container glassfish6x
[INFO] [talledLocalContainer] Parsed GlassFish version = [6.0.0]
[INFO] [talledLocalContainer] GlassFish 6.0.0 starting...
[INFO] [talledLocalContainer] Using port 4848 for Admin.
[INFO] [talledLocalContainer] Using port 8080 for HTTP Instance.
[INFO] [talledLocalContainer] Using port 7676 for JMS.
[INFO] [talledLocalContainer] Using port 3700 for IIOP.
[INFO] [talledLocalContainer] Using port 8181 for HTTP_SSL.
[INFO] [talledLocalContainer] Using port 3820 for IIOP_SSL.
[INFO] [talledLocalContainer] Using port 3920 for IIOP_MUTUALAUTH.
[INFO] [talledLocalContainer] Using port 8686 for JMX_ADMIN.
[INFO] [talledLocalContainer] Using port 6666 for OSGI_SHELL.
[INFO] [talledLocalContainer] Using port 9009 for JAVA_DEBUGGER.
[INFO] [talledLocalContainer] Distinguished Name of the self-signed X.509 Server Certificate is:
[INFO] [talledLocalContainer] [CN=hantsy-t540p,OU=GlassFish,O=Eclipse.org Foundation Inc,L=Ottawa,ST=Ontario,C=CA]
[INFO] [talledLocalContainer] Distinguished Name of the self-signed X.509 Server Certificate is:
[INFO] [talledLocalContainer] [CN=hantsy-t540p-instance,OU=GlassFish,O=Eclipse.org Foundation Inc,L=Ottawa,ST=Ontario,C=CA]
[INFO] [talledLocalContainer] Domain cargo-domain created.
[INFO] [talledLocalContainer] Domain cargo-domain admin port is 4848.
[INFO] [talledLocalContainer] Domain cargo-domain admin user is "admin".
[INFO] [talledLocalContainer] Command create-domain executed successfully.
[INFO] [talledLocalContainer] Attempting to start cargo-domain.... Please look at the server log for more details.....
[INFO] [talledLocalContainer] remote failure: The jdbc resource [ jdbc/__default ] cannot be deleted as it is required to be configured in the system.
[INFO] [talledLocalContainer] Command delete-jdbc-resource failed.
[INFO] [talledLocalContainer] JDBC Connection pool DerbyPool deleted successfully
[INFO] [talledLocalContainer] Command delete-jdbc-connection-pool executed successfully.
[INFO] [talledLocalContainer] Application deployed with name jakartaee9-starter-boilerplate.
[INFO] [talledLocalContainer] Command deploy executed successfully.
[INFO] [talledLocalContainer] Application deployed with name cargocpc.
[INFO] [talledLocalContainer] Command deploy executed successfully.
[INFO] [talledLocalContainer] GlassFish 6.0.0 started on port [8080]
[INFO] Press Ctrl-C to stop the container...
```

As the GlassFish 6.0.0 artifacts are available on the maven central, the above command can be replaced following config in the project *pom.xml* file.

```xml
 <properties>
	<cargo.servlet.port>8080</cargo.servlet.port>
</properties>

<build>
	<plugins>
		<plugin>
			<groupId>org.codehaus.cargo</groupId>
			<artifactId>cargo-maven3-plugin</artifactId>
			<configuration>
				<container>
					<containerId>glassfish6x</containerId>
					<artifactInstaller>
                        <groupId>org.glassfish.main.distributions</groupId>
                        <artifactId>glassfish</artifactId>
                        <version>6.0.0</version>
					</artifactInstaller>
				</container>
				<configuration>
					<home>${project.build.directory}/glassfish6x-home</home>
					<properties>
						<cargo.servlet.port>${cargo.servlet.port}</cargo.servlet.port>
					</properties>
				</configuration>
			</configuration>
		</plugin>
	</plugins>
</build>
```

You can run the following command instead with the above configuration.

```bash
mvn clean verify cargo:run
```

Here we use the `run` goal, it will keep the application server running until we sending a `CTRL+C` command to stop it.

If you have prepared a copy of Glassfish v6,  you can reuse the **existing** Glassfish and domain configuration.

```xml
<properties>
	<glassfish.home>[ The existing glassfish6 dir]</glassfish.home>
	<glassfish.domainDir>${glassfish.home}/glassfish/domains</glassfish.domainDir>
	<glassfish.domainName>domain1</glassfish.domainName>
</properties>
<build>
	<plugins>
		<plugin>
			<groupId>org.codehaus.cargo</groupId>
			<artifactId>cargo-maven3-plugin</artifactId>
			<configuration>
				<container>
					<containerId>glassfish6x</containerId>
					<type>installed</type>
					<home>${glassfish.home}</home>
				</container>
				<configuration>
					<type>existing</type>
					<home>${glassfish.domainDir}</home>
					<properties>
						<cargo.glassfish.domain.name>${glassfish.domainName}</cargo.glassfish.domain.name>
						<cargo.remote.password></cargo.remote.password>
					</properties>
				</configuration>
			</configuration>
		</plugin>
	</plugins>
</build>
```

In the above codes, set the configuration type to **existing**, and specify the domain name to the default `domain1`, when run the following command it will reuse the existing `domain1` to deploy the applications.

```bash
mvn clean verify cargo:run
```

If you want to deploy your applications to a running Glassfish v6 server(esp. it is running on a different machine), try to configure a **runtime** configuration and use a **remote** deployer to perform the deployment.

```xml
<plugin>
	<groupId>org.codehaus.cargo</groupId>
	<artifactId>cargo-maven3-plugin</artifactId>
	<configuration>
		<container>
			<containerId>glassfish5x</containerId>
			<type>remote</type>
		</container>
		<configuration>
			<type>runtime</type>
			<properties>
				<cargo.remote.username>admin</cargo.remote.username>
				<cargo.remote.password></cargo.remote.password>
				<cargo.glassfish.admin.port>4848</cargo.glassfish.admin.port>
				<cargo.hostname>localhost</cargo.hostname>
			</properties>
		</configuration>
	</configuration>
	<!-- provides JSR88 client API to deploy on Glassfish/Payara Server -->
	<!-- WARNING: deployment client is not available in Glassfish v6.0 -->
	<dependencies>
		<dependency>
			<groupId>org.glassfish.main.deployment</groupId>
			<artifactId>deployment-client</artifactId>
			<version>5.1.0</version>
			<!--<version>${glassfish.version}</version>-->
		</dependency>
	</dependencies>
</plugin>
```

 In the  above configuration:

* `cargo.hostname` is the target server you want to deploy to 
* `cargo.remote.username` and `cargo.remote.password` is administrator account used to deploy
* The remote deployer depends on a `deployment-client` archetype, here we use the version `5.1.0`,  there is no new version for Glassfish v6, and JSR 88 spec is removed in Jakarta EE 9. That's also why we use the `glassfish5x` container of Cargo (instead of `glassfish6x`)

For a remote container, you can not control the start and stop lifecycle as the former configurations, use `deploy` and `undeploy` goal to perform the deploy and undeploy tasks.

```bash
# deploy applications
mvn clean verify cargo:deploy

# undeploy
mvn cargo:undeploy
```

When the application is deployed successfully, use `curl` to verify if the deployed application is running.

```bash
curl http://localhost:8080/jakartaee9-starter-boilerplate/api/greeting/Hantsy
{"message":"Say Hello to Hantsy at 2020-11-14T15:56:10.099"}
```

I have added the above configurations with different Maven profiles in the *pom.xml*.  Check out the [source codes](https://github.com/hantsy/jakartaee9-starter-boilerplate/) and explore yourself.
