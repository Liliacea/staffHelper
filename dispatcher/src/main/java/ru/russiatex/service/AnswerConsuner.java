package ru.russiatex.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface AnswerConsuner {
    public void consume (SendMessage sendMessage);
}
