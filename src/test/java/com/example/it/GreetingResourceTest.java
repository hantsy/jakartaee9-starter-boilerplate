package com.example.it;

import com.example.GreetingMessage;
import com.example.GreetingResource;
import com.example.GreetingService;
import com.example.JaxrsActivator;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class GreetingResourceTest {
    private final static Logger LOGGER = Logger.getLogger(GreetingResourceTest.class.getName());

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(GreetingMessage.class)
                .addClass(GreetingService.class)
                .addClasses(GreetingResource.class, JaxrsActivator.class)
                // Enable CDI
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @ArquillianResource
    private URL base;

    private Client client;

    @Before
    public void setup() {
        this.client = ClientBuilder.newClient();
        try {
            LOGGER.log(Level.INFO, " Registering 'com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider' in OpenLiberty/CXF JAX-RS Client ");
            Class<?> clazz = Class.forName("com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider");
            this.client.register(clazz);
        } catch (ClassNotFoundException e) {
            LOGGER.warning("Failed to register 'com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider'. OpenLiberty/CXF does not register a json provider automatically. Please ignore this warning for none OpenLiberty Servers.");
        }

    }

    @After
    public void teardown() {
        if (this.client != null) {
            this.client.close();
        }
    }

    @Test
    public void should_create_greeting() throws MalformedURLException {
        LOGGER.log(Level.INFO, " Running test:: GreetingResourceTest#should_create_greeting ... ");
        final WebTarget greetingTarget = client.target(new URL(base, "api/greeting/JakartaEE").toExternalForm());
        try (final Response greetingGetResponse = greetingTarget.request()
                .accept(MediaType.APPLICATION_JSON)
                .get()) {
            assertEquals("response status is ok", 200, greetingGetResponse.getStatus());
            assertTrue("message should start with \"Say Hello to JakartaEE at \"",
                    greetingGetResponse.readEntity(GreetingMessage.class).getMessage().startsWith("Say Hello to JakartaEE"));

        }
    }
}
