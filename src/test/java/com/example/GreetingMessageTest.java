package com.example;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class GreetingMessageTest {

    @Test
    public void testGreetingMessage() {
        var message = GreetingMessage.of("Say Hello to JatartaEE");
        assertThat(message.getMessage()).isEqualTo("Say Hello to JatartaEE");
    }
}
