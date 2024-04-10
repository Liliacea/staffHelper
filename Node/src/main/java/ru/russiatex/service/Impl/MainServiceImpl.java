package ru.russiatex.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.russiatex.commonjpa.entity.AppDocument;
import ru.russiatex.commonjpa.entity.AppPhoto;
import ru.russiatex.dao.RawDataDao;
import ru.russiatex.commonjpa.dao.AppUserDao;
import ru.russiatex.commonjpa.entity.AppUser;
import ru.russiatex.commonjpa.entity.enums.UserState;
import ru.russiatex.entity.RawData;
import ru.russiatex.exceptions.UploadFileException;
import ru.russiatex.service.FileService;
import ru.russiatex.service.MainService;
import ru.russiatex.service.ProduserService;
import ru.russiatex.service.ServiceCommands;

import static ru.russiatex.commonjpa.entity.enums.UserState.BASIC_STATE;
import static ru.russiatex.commonjpa.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static ru.russiatex.model.RabbitQueue.textMessage;
import static ru.russiatex.service.ServiceCommands.*;

@Service
@Log4j
public class MainServiceImpl implements MainService {
    private final RawDataDao rawDataDao;
    private final ProduserService produserService;
    private final AppUserDao appUserDao;
    private final FileService fileService;

    public MainServiceImpl(RawDataDao rawDataDao, ProduserService produserService, AppUserDao appUserDao, FileService fileService) {
        this.rawDataDao = rawDataDao;
        this.produserService = produserService;
        this.appUserDao = appUserDao;
        this.fileService = fileService;
    }

    @Override
    public void processTextMessage(Update update) {
    saveRawData(update);
    //var textMessage = update.getMessage();
    //var telegramUser = textMessage.getFrom();

    var appUser = findOrSaveAppUser(update);
    var userState = appUser.getState();
    var text = update.getMessage().getText();
    var output = " ";
//    var serviceCommand = ServiceCommands.fromValue(text);
        //TODO добавить команды на скачивание документов и переписать на swith case
    if(CANCEL.equals(text)){
        output = cancelProcess(appUser);
    } else if (BASIC_STATE.equals(userState)) {
        output = processServiceCommand(appUser, text);
    } else if (WAIT_FOR_EMAIL_STATE.equals(userState)){
        //TODO добавить обработку регистрации и разграничить права пользователей
    }else {
        log.error("Unknown user state: " + userState);
        output = "Неизвестная ошибка! Введите /cancel и попробуйте снова!";
    }
var chatId = update.getMessage().getChatId();
    sendAnswer(output,chatId);

     /*   var message = update.getMessage();
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText("hello from node");

        produserService.produseAnswer(sendMessage);

      */
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)){
            return;
        }
        try {
            AppDocument doc = fileService.processDoc(update.getMessage());

            var answer = "Документ успешно загружен. ссылка для скачивания: ";
            sendAnswer(answer, chatId);
        }catch (UploadFileException ex){
            log.error(ex);
            String error = "к сожалению, загрузка файла не удалась. Попробуйте позже";
            sendAnswer(error,chatId);

        }
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getState();
        if(!appUser.getIsActive()){
            var error = "пройдите регистрацию";
            sendAnswer(error,chatId);
            return true;

        } else if (!BASIC_STATE.equals(userState)) {
            var error = "отмените текущую команду командой /cancel для отправки файлов";
            sendAnswer(error,chatId);
            return true;

        }
        return false;
    }

    @Override
    public void processPhotoMessage(Update update) {
saveRawData(update);
var appUser = findOrSaveAppUser(update);
var chatId = update.getMessage().getChatId();
if (isNotAllowToSendContent(chatId,appUser)){
    return;
}
try{
    AppPhoto appPhoto = fileService.processPhoto(update.getMessage());
    //TODO добавить генерацию ссылки
    var answer = "фото успешно загружено. Ссылка для скачивания  ";
    sendAnswer(answer,chatId);
} catch (UploadFileException ex){
    log.error(ex);
    String error = "к сожалению, загрузка файла не удалась. Попробуйте позже";
    sendAnswer(error,chatId);
}
    }

    private void sendAnswer(String output, Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);

        produserService.produseAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String cmd) {
        if(REGISTRATION.equals(cmd)){
            //TODO добавить регистрацию
    return "Команда временно недоступна";
        } else if (HELP.equals(cmd)) {
            return help();
        } else if (START.equals(cmd)) {
            return "для просмотра доступных команд введите /help";

        } else {
         return    "неизвестная команда. введите /help";
        }
    }

    private String help() {
    return "Список доступных команд \n"

            + "/cancel \n"
            + "/registration \n";

    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDao.save(appUser);
        return "Команда отменена";
    }

    private AppUser findOrSaveAppUser(Update update){
        User telegramUser = update.getMessage().getFrom();
        AppUser persistentAppUser = appUserDao.findAppUserByTelegramUserId(telegramUser.getId());
        if(persistentAppUser==null){
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstname(telegramUser.getFirstName())
                    .lastname(telegramUser.getLastName())
                    //TODO изменить значение после добавления регистрации
                    .isActive(true)
                    .state(BASIC_STATE)
                    .build();
            return appUserDao.save(transientAppUser);
        }
        return  persistentAppUser;
    }




    public void saveRawData(Update update){
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDao.save(rawData);
    }


}
