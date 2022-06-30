package pers.oscar.mcplugin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pers.oscar.mcplugin.service.UploadService;

import java.util.Map;

@RestController
@RequestMapping("/upload")
@CrossOrigin(origins="*")
public class UploadController {
    private UploadService uploadService;

    @Autowired
    public void setUploadService(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping
    public Map<String, String> uploadImage(@RequestPart(value = "file") MultipartFile file) {
        String file_name = uploadService.uploadPicture(file);
        return Map.of("file", file_name);
    }
}
