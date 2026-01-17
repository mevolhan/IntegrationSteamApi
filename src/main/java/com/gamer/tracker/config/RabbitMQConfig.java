package com.gamer.tracker.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ACHIEVEMENT_EXCHANGE = "achievement-exchange";
    public static final String ACHIEVEMENT_QUEUE = "achievement-queue";
    public static final String NOTIFICATION_QUEUE = "notification-queue";
    public static final String ACHIEVEMENT_ROUTING_KEY = "achievement.#";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.#";

    @Bean
    public TopicExchange achievementExchange() {
        return new TopicExchange(ACHIEVEMENT_EXCHANGE);
    }

    @Bean
    public Queue achievementQueue() {
        return new Queue(ACHIEVEMENT_QUEUE, true);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    @Bean
    public Binding achievementBinding() {
        return BindingBuilder
                .bind(achievementQueue())
                .to(achievementExchange())
                .with(ACHIEVEMENT_ROUTING_KEY);
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder
                .bind(notificationQueue())
                .to(achievementExchange())
                .with(NOTIFICATION_ROUTING_KEY);
    }

    // Убрали messageConverter и rabbitTemplate - Spring Boot настроит их автоматически
}