package pers.oscar.mcplugin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.oscar.mcplugin.entity.PluginDocument;
import pers.oscar.mcplugin.service.DocumentService;

import java.util.Map;

@RestController
@RequestMapping("/document")
public class DocumentController {
    private DocumentService documentService;

    @Autowired
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(value = "/save")
    public void saveDocument(@RequestBody @Validated PluginDocument pluginDocument) {
        documentService.saveDocument(pluginDocument);
    }

    @PostMapping(value = "/create")
    public void createDocument(@RequestBody @Validated PluginDocument pluginDocument) {
        documentService.createDocument(pluginDocument);
    }

    @GetMapping(value = "/{id}")
    public PluginDocument getDocumentByID(@PathVariable long id) {
        return documentService.getDocumentByID(id);
    }

    @GetMapping(value = "/{id}/info")
    public PluginDocument getDocumentInfo(@PathVariable long id) {
        return documentService.getDocumentInfo(id);
    }

    @GetMapping("/user/{id}/{page}/{size}")
    public Map<String, Object> getMyDocument(@PathVariable Integer id, @PathVariable Integer page, @PathVariable Integer size) {
        return documentService.getUserDocument(id, page, size);
    }

}
