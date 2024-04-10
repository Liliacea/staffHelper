package ru.russiatex;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Component
@Log4j
public class Telegrambot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;
    private UpdateProcessor updateProcessor;

    public Telegrambot(UpdateProcessor updateProcessor) {
        this.updateProcessor = updateProcessor;
    }
    @PostConstruct
    public void init(){
        updateProcessor.registerBot(this);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateProcessor.processUpdate(update);
    }


    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }
}
