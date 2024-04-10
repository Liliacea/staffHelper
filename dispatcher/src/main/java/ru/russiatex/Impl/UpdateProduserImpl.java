package ru.russiatex.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.russiatex.service.UpdateProduser;
@Log4j
@RequiredArgsConstructor
@Service
public class UpdateProduserImpl implements UpdateProduser {
    private final RabbitTemplate rabbitTemplate;
    @Override
    public void produse(String rabbitQueue, Update update) {
        rabbitTemplate.convertAndSend(rabbitQueue,update);
        log.debug(update.getMessage().getText());
    }
}
