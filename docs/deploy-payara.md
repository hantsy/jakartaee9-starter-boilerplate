# Deploying Jakarta EE 9 applications to Payara using Cargo maven plugin

Payara Community 5.2020.5 has introduced tech preview functionality to run Jakarta EE 9 on Payara Server and Micro, more details please go to the [release notes](https://docs.payara.fish/community/docs/5.2020.6/release-notes/release-notes-2020-5.html#_run_jakarta_ee_9_applications_in_tech_preview).

Payara is driven from Glassfish project, but it includes a bundle of new features that not existed in Glassfish.

* Numerous improvements and quicker bugfixes in comparison to the existing Glassfish
* Java 11(or above) support 
* Built-in Microprofile support and ready for cloud native applications
* Many third-party services integration. 
* Comprehensive documentation and technical guides
* Commercial support available for paid enterprise users. 

Payara is also an open source project, for developers, you use Community edition as an alternative to Glassfish to get better development experience. 

## Prerequisites

Make sure you have installed the following software.

* Java 8 or Java 11 ([OpenJDK](https://openjdk.java.net/install/) or [AdoptOpenJDK](https://adoptopenjdk.net/installation.html))
* [Payara Community 5.2020.6](https://www.payara.fish/downloads/payara-platform-community-edition/)
* [Apache Maven](http://maven.apache.org/) 
* Get to know the basic of [Cargo maven plugin](https://codehaus-cargo.github.io/). 

## Deploy to local Payara server

Cargo maven plugin provides a separate **payara** container for Payara server.

Assume you have downloaded a copy of  [Payara Community  dist](https://www.payara.fish/downloads/payara-platform-community-edition/), and extracted the files into your local disk.

Add the following configuration in *pom.xml* to use the existing Payara domain configuration to run your Jakarta EE 9 applications on  the local Payara server. 

```xml
<properties>
	<glassfish.home>[ Payara install dir]</glassfish.home>
	<glassfish.domainDir>${glassfish.home}/glassfish/domains</glassfish.domainDir>
	<glassfish.domainName>domain1</glassfish.domainName>
</properties>
<build>
	<plugins>
		<plugin>
			<groupId>org.codehaus.cargo</groupId>
			<artifactId>cargo-maven2-plugin</artifactId>
			<configuration>
				<container>
					<containerId>payara</containerId>
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

The above configuration is almost same as the one we have discussed in [previous Glassfish post](./docs/deploy-cargo.md), here we used containerId **payara**.

Run the following command to deploy your application to the Payara server.

```bash
mvn clean package cargo:run -Ppayara-local
```

It will build the project and packaged it into a war in the target folder, then starts up Payara server using the existing *domain1* configuration, and finally deploy the war to the running server.

If you want to use a refresh domain configuration to run your applications,  try to set the configuration **type** to **standalone**.  Change the server *configuration* section to the following.

```xml
<configuration>
    <type>standalone</type>
    <properties>
        <cargo.remote.password></cargo.remote.password>
    </properties>
</configuration>
```

It will create a new domain configuration before starting Payara server.

In a CI server, you can use a *Dependency maven plugin* to prepare a refresh copy of Payara server.

```xml
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
```

Change the property `glassfish.home` to the following.

```xml
<glassfish.home>${project.build.directory}/payara5</glassfish.home>
```

To stop the server, just send a `CTRL+C` to the running console to stop it.



## Deploy to a running Payara server

Use a remote deployer and runtime configuration, you can use Cargo maven plugin to deploy the application to a running Payara, esp. the Payara server is located in a remote server.

```xml
<plugin>
	<groupId>org.codehaus.cargo</groupId>
	<artifactId>cargo-maven2-plugin</artifactId>
	<configuration>
		<container>
			<containerId>payara</containerId>
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

 The above configuration is almost same as the one from [Glassfish doc](./docs/deploy-cargo.md).

For a remote container, you can not start and stop the Payara server. Make sure it is running, use `deploy` and `undeploy` goals to perform the deploy and undeploy tasks.

```bash
# deploy applications
mvn clean package cargo:deploy -Ppayara-remote

# undeploy
mvn cargo:undeploy -Ppayara-remote
```



## Verifying the deployed application

When the application is deployed successfully, use `curl` to verify if the deployed application is running.

```bash
curl http://localhost:8080/jakartaee9-starter-boilerplate/api/greeting/Hantsy
{"message":"Say Hello to Hantsy at 2020-11-14T15:56:10.099"}
```

> Note: In fact, you can use `glassfish5x` containerId  to deploy Jakarta EE 9 applications to Payara server, and in reverse I think it is also fine when using `payara` containerId to deploy the application to Glassfish v6.

Grab the [source codes](https://github.com/hantsy/jakartaee9-starter-boilerplate/) from my github.

