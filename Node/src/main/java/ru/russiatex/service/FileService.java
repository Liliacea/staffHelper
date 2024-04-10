package ru.russiatex.service;


import org.telegram.telegrambots.meta.api.objects.Message;
import ru.russiatex.commonjpa.entity.AppDocument;
import ru.russiatex.commonjpa.entity.AppPhoto;


public interface FileService {
    AppDocument processDoc (Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
}