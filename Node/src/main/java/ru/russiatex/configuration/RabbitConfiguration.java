package ru.russiatex.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static ru.russiatex.model.RabbitQueue.*;


@Configuration
public class RabbitConfiguration {




    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue textMessageQueue() {
        return new Queue(textMessage);
    }

    @Bean
    public Queue docMessageQueue() {
        return new Queue(docMessage);
    }

    @Bean
    public Queue photoMessageQueue() {
        return new Queue(photoMessage);
    }

    @Bean
    public Queue answerMessageQueue() {
        return new Queue(answerMessage);
    }
}
