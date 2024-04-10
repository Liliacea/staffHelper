package ru.russiatex.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateProduser {
    public void produse (String rabbitQueue, Update update);
}
