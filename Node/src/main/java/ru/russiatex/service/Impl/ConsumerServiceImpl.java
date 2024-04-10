package ru.russiatex.service.Impl;

import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.russiatex.service.ConsumerService;
import ru.russiatex.service.MainService;
import ru.russiatex.service.ProduserService;

import static ru.russiatex.model.RabbitQueue.*;

@Service
@Log4j
public class ConsumerServiceImpl implements ConsumerService {
    private final MainService mainService;

    public ConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    @RabbitListener(queues = textMessage)
    public void consumeTextMessageUpdate(Update update) {
        log.debug("Node: text is receved");
        mainService.processTextMessage(update);
    }

    @Override
    @RabbitListener(queues = docMessage)
    public void consumeDocMessageUpdate(Update update) {
        log.debug("Node: document is receved");
        mainService.processDocMessage(update);

    }

    @Override
    @RabbitListener(queues = photoMessage)
    public void consumePhotoMessageUpdate(Update update) {
    log.debug("Node: photo is receved");
    mainService.processPhotoMessage(update);
    }
}
