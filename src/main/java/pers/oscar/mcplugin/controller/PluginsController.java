package pers.oscar.mcplugin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.oscar.mcplugin.entity.Plugin;
import pers.oscar.mcplugin.service.PluginService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/plugins")
public class PluginsController {

    private PluginService pluginService;

    @Autowired
    public void setPluginService(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @PostMapping(value = "/save")
    public boolean savePlugin(@RequestBody @Validated Plugin plugin) {
        pluginService.savePlugin(plugin);
        return true;
    }

    @PostMapping(value = "/create")
    public boolean createPlugin(@RequestBody @Validated Plugin plugin) {
        pluginService.createPlugin(plugin);
        return true;
    }

    @GetMapping(value = "/{id}")
    public Plugin getPluginByID(@PathVariable long id) {
        return pluginService.getPluginByID(id);
    }

    @GetMapping(value = "/{id}/info")
    public Plugin getPluginInfo(@PathVariable long id) {
        return pluginService.getPluginInfo(id);
    }

    @GetMapping(value = "/page/{page}/{size}")
    public Map<String, Object> getPluginPage(@PathVariable Integer page, @PathVariable Integer size) {
        return pluginService.getPluginPage(page, size);
    }

    @GetMapping("/user/{id}/{page}/{size}")
    public Map<String, Object> getMyPlugins(@PathVariable Integer id, @PathVariable Integer page, @PathVariable Integer size) {
        return pluginService.getMyPlugins(id, page, size);
    }

    @PostMapping("/fuzzy")
    public Map<String, Object> fuzzySearch(@RequestBody Map<String, String> info) {
        Integer page = Integer.parseInt(info.get("page"));
        Integer size = Integer.parseInt(info.get("size"));
        return pluginService.searchAll(info.get("info"), page, size);
    }

    @PostMapping("/exact")
    public Map<String, Object> exactSearch(@RequestBody Map<String, Object> info) {
        String name = (String) info.get("name");
        String pluginType = (String) info.get("pluginType");
        String serverType = (String) info.get("serverType");
        String serverVersion = (String) info.get("serverVersion");
        Integer page = (Integer) info.get("page");
        Integer size = (Integer) info.get("size");
        return pluginService.searchPlugin(name,
                                        pluginType,
                                        serverType,
                                        serverVersion,
                                        page,
                                        size);
    }

    @GetMapping("/hot")
    public List<Plugin> getHotPlugin() {
        return pluginService.getHotPlugin();
    }

}
