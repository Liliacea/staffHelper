package ru.russiatex.service;

import org.springframework.core.io.FileSystemResource;
import ru.russiatex.commonjpa.entity.AppDocument;
import ru.russiatex.commonjpa.entity.AppPhoto;
import ru.russiatex.commonjpa.entity.BinaryContent;

import java.io.IOException;

public interface FileService {
    AppDocument getDocument (String id);
    AppPhoto getPhoto (String id);
    FileSystemResource getFileSystemResource (BinaryContent binaryContent);
}
