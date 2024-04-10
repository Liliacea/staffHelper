package ru.russiatex.service.Impl;

import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import ru.russiatex.commonjpa.dao.AppDocumentDAO;
import ru.russiatex.commonjpa.dao.AppPhotoDAO;
import ru.russiatex.commonjpa.entity.AppDocument;
import ru.russiatex.commonjpa.entity.AppPhoto;
import ru.russiatex.commonjpa.entity.BinaryContent;
import ru.russiatex.service.FileService;

import java.io.File;
import java.io.IOException;

import static java.io.File.createTempFile;

@Service
@Log4j
public class FileServiceImpl implements FileService {
    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO) {
        this.appDocumentDAO = appDocumentDAO;
        this.appPhotoDAO = appPhotoDAO;
    }

    @Override
    public AppDocument getDocument(String id) {
        var fileId = Long.parseLong(id);
        return appDocumentDAO.findById(fileId).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String id) {
        var fileId = Long.parseLong(id);
        return appPhotoDAO.findById(fileId).orElse(null);
    }

    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent)  {

        try {
           File temp = createTempFile ("tempFile","bin");
            temp.deleteOnExit();
            FileUtils.writeByteArrayToFile(temp,binaryContent.getFileAsArrayOfBytes());
            return new FileSystemResource(temp);
        } catch (IOException e) {
            log.error(e);
            return null;
        }



    }
}
