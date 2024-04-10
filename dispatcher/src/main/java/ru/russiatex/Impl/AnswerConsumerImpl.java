package ru.russiatex.Impl;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.russiatex.UpdateProcessor;
import ru.russiatex.service.AnswerConsuner;

import static ru.russiatex.model.RabbitQueue.answerMessage;

@Service
public class AnswerConsumerImpl implements AnswerConsuner {
    private UpdateProcessor updateProcessor;

    public AnswerConsumerImpl(UpdateProcessor updateProcessor) {
        this.updateProcessor = updateProcessor;
    }

    @Override
    @RabbitListener(queues = answerMessage)
    public void consume(SendMessage sendMessage) {
    updateProcessor.setView(sendMessage);
    }
}
