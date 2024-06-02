package com.devomate.videoedit.microservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    @Value("${rabbitmq.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.tasks-queue}")
    private String tasksQueueName;

    @Value("${rabbitmq.tasks-routing-key}")
    private String tasksRoutingKey;

    @Value("${rabbitmq.answers-queue}")
    private String answersQueueName;

    @Value("${rabbitmq.answers-routing-key}")
    private String answersRoutingKey;

    @Bean
    public Queue tasksQueue() {
        return new Queue(tasksQueueName, true);
    }

    @Bean
    public Queue answersQueue() {
        return new Queue(answersQueueName, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Binding tasksBinding(@Qualifier("tasksQueue") Queue tasksQueue, DirectExchange exchange) {
        return BindingBuilder.bind(tasksQueue).to(exchange).with(tasksRoutingKey);
    }

    @Bean
    public Binding answersBinding(@Qualifier("answersQueue") Queue answersQueue, DirectExchange exchange) {
        return BindingBuilder.bind(answersQueue).to(exchange).with(answersRoutingKey);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        int cpuCores = Runtime.getRuntime().availableProcessors();

        var factory = new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(cpuCores);

        return factory;
    }
}
