package ru.russiatex.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ProduserService {
    void produseAnswer(SendMessage sendMessage);

}
