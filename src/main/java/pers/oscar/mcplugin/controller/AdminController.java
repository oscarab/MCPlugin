package pers.oscar.mcplugin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.oscar.mcplugin.service.CommentService;
import pers.oscar.mcplugin.service.DocumentService;
import pers.oscar.mcplugin.service.PluginService;
import pers.oscar.mcplugin.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private PluginService pluginService;
    private DocumentService documentService;
    private CommentService commentService;
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
    }

    @Autowired
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Autowired
    public void setPluginService(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @GetMapping("/plugin/{id}")
    public void deletePlugin(@PathVariable Integer id) {
        pluginService.deletePlugin(id);
    }

    @GetMapping("/document/{id}")
    public void deleteDocument(@PathVariable Integer id) {
        documentService.deleteDocument(id);
    }

    @GetMapping("/comment/{id}")
    public void deleteComment(@PathVariable Integer id) {
        commentService.deleteComment(id);
    }

    @PostMapping("/role")
    public void editRole(@RequestBody Map<String, String> info) {
        userService.editUserRole(
                Long.parseLong(info.get("id")),
                info.get("role")
        );
    }
}
