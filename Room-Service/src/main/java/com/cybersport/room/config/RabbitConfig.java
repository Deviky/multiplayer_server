package com.cybersport.room.config;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

@Configuration
@EnableRabbit
public class RabbitConfig {

    @Bean
    public Queue gameQueue() {
        return new Queue("game_queue", true);
    }


}