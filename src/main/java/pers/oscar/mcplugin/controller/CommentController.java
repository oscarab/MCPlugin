package pers.oscar.mcplugin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.oscar.mcplugin.entity.Comment;
import pers.oscar.mcplugin.service.CommentService;

import java.util.Map;

@RestController
@RequestMapping("/comment")
public class CommentController {
    private CommentService commentService;

    @Autowired
    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/create")
    public boolean createComment(@RequestBody @Validated Comment comment) {
        commentService.createComment(comment);
        return true;
    }

    @GetMapping(value = "/{id}/page/{page}/{size}")
    public Map<String, Object> getComments(@PathVariable Long id, @PathVariable Integer page, @PathVariable Integer size) {
        return commentService.getCommentsInPlugin(id, page, size);
    }
}
