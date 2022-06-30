package pers.oscar.mcplugin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pers.oscar.mcplugin.entity.Comment;
import pers.oscar.mcplugin.entity.Plugin;
import pers.oscar.mcplugin.entity.User;
import pers.oscar.mcplugin.repository.CommentRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {
    private PluginService pluginService;
    private CommentRepository commentRepository;
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setCommentRepository(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Autowired
    public void setPluginService(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    /**
     * 创建评论
     * @param comment 评论
     */
    public void createComment(Comment comment) {
        User me = userService.getCurrentDetail();
        comment.setPublishTime(new Date());
        comment.setUser(me);
        commentRepository.save(comment);
    }

    /**
     * 获取插件下的所有评论
     * @param id
     * @return
     */
    public Map<String, Object> getCommentsInPlugin(Long id, Integer page, Integer size) {
        Plugin plugin = pluginService.getPluginByID(id);
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.ASC,"publishTime"));
        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
        Page<Comment> pages = commentRepository.findByPlugin(plugin, pageable);

        return Map.of("total", pages.getTotalPages(), "page", pages.getContent());
    }

    /**
     * 删除评论
     * @param id 评论id
     */
    public void deleteComment(long id) {
        commentRepository.deleteComment(id);
    }
}
