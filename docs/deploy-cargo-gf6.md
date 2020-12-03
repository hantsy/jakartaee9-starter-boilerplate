# Remote Deployment to Glassfish v6 using Cargo Local Deployer

> This is an addition to the existing [deploying Jakarta EE 9 applications to Glassfish v6 using Cargo maven plugin](./docs/deploy-cargo.md).

[Cargo maven plugin](https://codehaus-cargo.github.io) 1.8.3 will include a `glasfish6x` *containerId* for the new Glassfish v6.  In 1.8.2 or previous versions, it allows you use a JSR88 specification(Deployment) based remote deployer with a *runtime* configuration to deploy applications to a running Glassfish server. 

This will stop to work when switching to use a `glassfish6x` container due to the changes happen in Jakarta EE 9 and Glassfish v6. 

The JSR88 is removed in the further Jakarta EE 9, check the [6.1.4. Removed Jakarta Technologies](https://jakarta.ee/specifications/platform/9/jakarta-platform-spec-9.html#a2333) section of  Jakarta EE 9 specification.  And the `deployment-client` artifact which is existed for Glassfish v5 release train is not available in Glassfish v6.

Correspondingly cargo `glassfish6x` container will not include a remote deployer, and you can not configure a *runtime* configuration as before. But you can use the local deployer to deploy the applications to remote servers.

```xml
<profile>
    <id>glassfish-local-deployer</id>
    <properties>
        <cargo.hostname>localhost</cargo.hostname>
        <cargo.servlet.port>8080</cargo.servlet.port>
        <cargo.glassfish.admin.port>4848</cargo.glassfish.admin.port>
        <cargo.zipUrlInstaller.downloadDir>${project.build.directory}/../installs
        </cargo.zipUrlInstaller.downloadDir>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>org.codehaus.cargo</groupId>
                <artifactId>cargo-maven2-plugin</artifactId>
                <configuration>
                    <container>
                        <containerId>glassfish6x</containerId>
                        <zipUrlInstaller>
                            <url>
                               https://download.eclipse.org/ee4j/glassfish/glassfish-${glassfish.version}.zip
                            </url>
                            <downloadDir>${cargo.zipUrlInstaller.downloadDir}</downloadDir>
                        </zipUrlInstaller>
                        <!-- or use artifactInstaller-->
                        <!--<artifactInstaller>
                                    <groupId>org.glassfish.main.distributions</groupId>
                                    <artifactId>glassfish</artifactId>
                                    <version>${glassfish.version}</version>
                                </artifactInstaller>-->
                    </container>
                    <configuration>
                        <!-- the configuration use to deploy -->
                        <home>${project.build.directory}/glassfish6x-home</home>
                        <properties>
                            <cargo.hostname>${cargo.hostname}</cargo.hostname>
                            <cargo.servlet.port>${cargo.servlet.port}</cargo.servlet.port>
                            <cargo.glassfish.admin.port>${cargo.glassfish.admin.port}
                            </cargo.glassfish.admin.port>
                            <cargo.remote.username>admin</cargo.remote.username>
                            <cargo.remote.password></cargo.remote.password>
                        </properties>
                    </configuration>
                </configuration>
            </plugin>
        </plugins>
    </build>
</profile>
```



> The above configuration also work in the existing `glassfish5x` containerId.

The side effect is you have to download a copy of Glassfish dist, the local deployer uses the Glassfish built-in `asadmin` tooling command to perform the deployment.  

> NOTE: The `cargo.hostname` is the hostname/address of the target Glassfish server to deploy.  

Execute the following command to package and deploy the application to the target server.

```bash
mvn clean package cargo:deploy -Pglassfish-local-deployer
```
To undeploy the application, execute the following command.

```bash
mvn  cargo:undeploy -Pglassfish-local-deployer
```

Check the Cargo official guide  for [the instructions of Remote Deployment to  Glassfish v6](https://codehaus-cargo.github.io/cargo/Remote+deployments+to+GlassFish+6.x.html) and [the changes of Cargo glassfish6x containerid](https://codehaus-cargo.github.io/cargo/GlassFish+6.x.html).

