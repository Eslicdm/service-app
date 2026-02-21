package com.eslirodrigues.member.pricing.config;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchange.pricing}")
    private String pricingExchange;

    @Value("${app.rabbitmq.queue.price-updated}")
    private String priceUpdatedQueue;

    @Value("${app.rabbitmq.routingkey.price-updated}")
    private String priceUpdatedRoutingKey;

    @Bean
    public TopicExchange pricingExchange() {
        return new TopicExchange(pricingExchange);
    }

    @Bean
    public Queue priceUpdatedQueue() {
        return new Queue(priceUpdatedQueue);
    }

    @Bean
    public Binding priceUpdatedBinding() {
        return BindingBuilder
                .bind(priceUpdatedQueue())
                .to(pricingExchange())
                .with(priceUpdatedRoutingKey);
    }

    @Bean
    public JacksonJsonMessageConverter producerJackson2MessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory
    rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        final var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(producerJackson2MessageConverter());
        return factory;
    }
}