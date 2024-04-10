package ru.russiatex.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import ru.russiatex.commonjpa.dao.AppDocumentDAO;
import ru.russiatex.commonjpa.dao.AppPhotoDAO;
import ru.russiatex.commonjpa.dao.BinaryContentDAO;
import ru.russiatex.commonjpa.entity.AppDocument;
import ru.russiatex.commonjpa.entity.AppPhoto;
import ru.russiatex.commonjpa.entity.BinaryContent;
import ru.russiatex.exceptions.UploadFileException;
import ru.russiatex.service.FileService;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
@Log4j

@Service
public class FileServiseImpl implements FileService {
    @Value("${bot.token}")
    private String botToken;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;

    @Value("${service.file_storage.uri}")
    private String fileStorageUri;

    private final AppDocumentDAO appDocumentDAO;
    private final BinaryContentDAO binaryContentDAO;
    private final AppPhotoDAO appPhotoDAO;

    public FileServiseImpl(AppDocumentDAO appDocumentDAO, BinaryContentDAO binaryContentDAO, AppPhotoDAO appPhotoDAO) {
        this.appDocumentDAO = appDocumentDAO;
        this.binaryContentDAO = binaryContentDAO;
        this.appPhotoDAO = appPhotoDAO;
    }

//TODO сделать ограничение на сохранение в БД только для админа
    @Override
    public AppDocument processDoc(Message telegramMessage) {
        Document telegramDocument = telegramMessage.getDocument();
        String fileId = telegramMessage.getDocument().getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode()==HttpStatus.OK){
            BinaryContent persistanseBinaryContent = getPersisanseBinaryContent (response);
            AppDocument transientAppDoc = buildTransientAppDoc (telegramDocument, persistanseBinaryContent);
            return appDocumentDAO.save(transientAppDoc);
        } else throw new UploadFileException("Bad response from telegram service " + response);

    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        int lastPhoto = telegramMessage.getPhoto().size() - 1;
        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(lastPhoto);
        String fileId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode()==HttpStatus.OK){
            BinaryContent persistanseBinaryContent = getPersisanseBinaryContent (response);
            AppPhoto transientAppPhoto = buildTransientAppPhoto (telegramPhoto, persistanseBinaryContent);
            return appPhotoDAO.save(transientAppPhoto);
        } else throw new UploadFileException("Bad response from telegram service " + response);
    }



    private BinaryContent getPersisanseBinaryContent(ResponseEntity<String> response) {
        JSONObject jsonObject = new JSONObject(response.getBody());
        String filePath = String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path")
        );
        byte [] fileInByte = downloadFile(filePath);
        BinaryContent transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
        BinaryContent persistanseBinaryContent = binaryContentDAO.save(transientBinaryContent);
        return persistanseBinaryContent;
    }

    private AppDocument buildTransientAppDoc(Document telegramDocument, BinaryContent persistanseBinaryContent) {
   return AppDocument.builder()
           .telegramFileId(telegramDocument.getFileId())
           .docName(telegramDocument.getFileName())
           .binaryContent(persistanseBinaryContent)
           .mimeType(telegramDocument.getMimeType())
           .fileSize(telegramDocument.getFileSize())
           .build();
    }
    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistanseBinaryContent) {
        return AppPhoto.builder()
                .telegramFileId(telegramPhoto.getFileId())

                .binaryContent(persistanseBinaryContent)

                .fileSize(telegramPhoto.getFileSize())
                .build();
    }
    private byte[] downloadFile(String filePath) {
        var fullUri = fileStorageUri.replace("{token}", botToken)
                .replace("{filePath}", filePath);
        URL urlObj = null;
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UploadFileException(e);
        }

        //TODO подумать над оптимизацией
        try (InputStream is = urlObj.openStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException(urlObj.toExternalForm(), e);
        }
    }


    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                botToken,
                fileId
        );
    }
}
