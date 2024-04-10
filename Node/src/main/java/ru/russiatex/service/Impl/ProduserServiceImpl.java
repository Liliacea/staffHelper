package ru.russiatex.service.Impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.russiatex.service.ProduserService;

import static ru.russiatex.model.RabbitQueue.answerMessage;

@Service
public class ProduserServiceImpl implements ProduserService {
    private final RabbitTemplate rabbitTemplate;

    public ProduserServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void produseAnswer(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(answerMessage, sendMessage);

    }
}
