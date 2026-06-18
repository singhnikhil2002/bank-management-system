package com.bank.cms.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.otp}")
    private String otpQueue;

    @Value("${rabbitmq.queue.notification}")
    private String notificationQueue;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing.otp}")
    private String otpRoutingKey;

    @Value("${rabbitmq.routing.notification}")
    private String notificationRoutingKey;

    // ── Queues ─────────────────────────────────────────────────────

    @Bean
    public Queue otpQueue() {
        // durable=true means queue survives RabbitMQ restart
        return new Queue(otpQueue, true);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(notificationQueue, true);
    }

    // ── Exchange ───────────────────────────────────────────────────

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    // ── Bindings (connect queue to exchange via routing key) ───────

    @Bean
    public Binding otpBinding() {
        return BindingBuilder
                .bind(otpQueue())
                .to(exchange())
                .with(otpRoutingKey);
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder
                .bind(notificationQueue())
                .to(exchange())
                .with(notificationRoutingKey);
    }

    // ── Message Converter (serialize as JSON) ──────────────────────

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}