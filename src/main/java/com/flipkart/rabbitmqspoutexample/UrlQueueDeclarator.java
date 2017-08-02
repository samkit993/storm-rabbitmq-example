package com.flipkart.rabbitmqspoutexample;

import com.rabbitmq.client.Channel;
import io.latent.storm.rabbitmq.Declarator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UrlQueueDeclarator implements Declarator {

    private final String exchange;
    private final String queue;

    public UrlQueueDeclarator(String exchange, String queue){
        this.exchange = exchange;
        this.queue = queue;
    }

    @Override
    public void execute(Channel channel){
        try {
            Map<String, Object> args = new HashMap<>();
            args.put("x-max-priority", 10);
            channel.queueDeclare(queue, true, false, false, args);
            channel.exchangeDeclare(exchange, "topic", true);
            channel.queueBind(queue, exchange, "");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing rabbitmq declarations.", e);
        }

    }
}

