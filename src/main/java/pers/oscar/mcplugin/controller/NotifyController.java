package pers.oscar.mcplugin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.oscar.mcplugin.entity.Notify;
import pers.oscar.mcplugin.service.NotifyService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notify")
public class NotifyController {
    private NotifyService notifyService;

    @Autowired
    public void setNotifyService(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @GetMapping("/{page}/{size}")
    public Map<String, Object> getMyNotifies(@PathVariable Integer page, @PathVariable Integer size) {
        return notifyService.getMyNotify(page, size);
    }
}
