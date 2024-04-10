package ru.russiatex.controller;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.russiatex.service.FileService;

import java.io.IOException;

@Log4j
@RequestMapping("/file")
@RestController
public class FileController {
private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

@RequestMapping(method = RequestMethod.GET, value = "/get-doc")
    public ResponseEntity<?> getDoc (@RequestParam("id") String id)  {
        var doc = fileService.getDocument(id);
        if(doc==null){
            return ResponseEntity.badRequest().build();
        }
        var binaryContent = doc.getBinaryContent();
        var fileSystemResourse = fileService.getFileSystemResource(binaryContent);
        if(fileSystemResourse==null){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getMimeType()))
                .header("content-disposition","attachement; filename " + doc.getDocName())
                .body(fileSystemResourse);

}
    @RequestMapping(method = RequestMethod.GET, value = "/get-photo")
    public ResponseEntity<?> getPhoto (@RequestParam("id") String id)  {
        var photo = fileService.getPhoto(id);
        if(photo==null){
            return ResponseEntity.badRequest().build();
        }
        var binaryContent = photo.getBinaryContent();
        var fileSystemResourse = fileService.getFileSystemResource(binaryContent);
        if(fileSystemResourse==null){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header("content-disposition","attachement" )
                .body(fileSystemResourse);

    }
}
