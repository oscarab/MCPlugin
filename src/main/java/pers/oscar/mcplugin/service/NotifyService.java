package pers.oscar.mcplugin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pers.oscar.mcplugin.entity.Notify;
import pers.oscar.mcplugin.entity.User;
import pers.oscar.mcplugin.repository.NotifyRepository;

import java.util.List;
import java.util.Map;

@Service
public class NotifyService {
    private NotifyRepository notifyRepository;
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setNotifyRepository(NotifyRepository notifyRepository) {
        this.notifyRepository = notifyRepository;
    }

    /**
     * 获取自己的合并通知
     * @return 通知集合
     */
    public Map<String, Object> getMyNotify(Integer page, Integer size) {
        User me = userService.getCurrentDetail();
        Pageable pageable = PageRequest.of(page, size);
        Page<Notify> pages = notifyRepository.getMyNotifies(me.getId(), pageable);
        List<Notify> notifies = pages.getContent();
        for (Notify notify : notifies) {
            String name = notify.getUser().getUsername();
            notify.getUser().setPassword(null);
            notify.getUser().setIp(null);
            notify.getPlugin().setPluginDocument(null);
            notify.getDocument().setPlugin(null);
            notify.getCopy().setPluginDocument(null);
        }
        return Map.of("total", pages.getTotalPages(), "page", notifies);
    }
}
