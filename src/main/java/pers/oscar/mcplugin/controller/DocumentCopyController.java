package pers.oscar.mcplugin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.oscar.mcplugin.entity.PluginDocumentCopy;
import pers.oscar.mcplugin.service.CopyService;

import java.util.Map;

@RestController
@RequestMapping("/copy")
public class DocumentCopyController {
    private CopyService copyService;

    @Autowired
    public void setCopyService(CopyService copyService) {
        this.copyService = copyService;
    }

    @GetMapping("/{id}/info")
    public PluginDocumentCopy getCopyInfo(@PathVariable Long id) {
        return copyService.getCopyInfo(id);
    }

    @GetMapping("/{id}")
    public PluginDocumentCopy getCopy(@PathVariable Long id) {
        return copyService.getCopy(id);
    }

    @GetMapping("/user/{id}/{page}/{size}")
    public Map<String, Object> getMyCopies(@PathVariable Integer id, @PathVariable Integer page, @PathVariable Integer size) {
        return copyService.getUserCopy(id, page, size);
    }

    @PostMapping("/create")
    public void createCopy(@RequestBody PluginDocumentCopy copy) {
        copyService.createCopy(copy);
    }

    @PostMapping("/save")
    public void saveCopy(@RequestBody PluginDocumentCopy copy) {
        copyService.saveCopy(copy);
    }

    @PostMapping("/merge")
    public void mergeCopy(@RequestBody Map<String, Long> info) {
        copyService.mergeDocument(info.get("notify_id"), info.get("accept"));
    }

    @PostMapping("/request")
    public void pullRequest(@RequestBody Map<String, Long> info) {
        copyService.pullRequest(info.get("doc_id"), info.get("copy_id"));
    }
}
