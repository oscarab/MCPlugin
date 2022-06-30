package pers.oscar.mcplugin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pers.oscar.mcplugin.entity.Plugin;
import pers.oscar.mcplugin.entity.PluginDocument;
import pers.oscar.mcplugin.entity.User;
import pers.oscar.mcplugin.exception.AccessDenyException;
import pers.oscar.mcplugin.exception.ResourceNotFoundException;
import pers.oscar.mcplugin.repository.DocumentCopyRepository;
import pers.oscar.mcplugin.repository.DocumentRepository;
import pers.oscar.mcplugin.repository.PluginRepository;

import java.util.*;

@Service
public class PluginService {
    private PluginRepository pluginRepository;
    private DocumentRepository documentRepository;
    private DocumentCopyRepository copyRepository;
    private UserService userService;

    @Autowired
    public void setCopyRepository(DocumentCopyRepository copyRepository) {
        this.copyRepository = copyRepository;
    }

    @Autowired
    public void setDocumentRepository(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Autowired
    public void setPluginRepository(PluginRepository pluginRepository) {
        this.pluginRepository = pluginRepository;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * 根据插件id获取其大概信息
     * @param id 插件id
     * @return 插件
     */
    public Plugin getPluginByID(long id) {
        Optional<Plugin> plugin = pluginRepository.findById(id);
        if (plugin.isPresent()) {
            return plugin.get();
        }
        throw new ResourceNotFoundException("该插件不存在");
    }

    /**
     * 保存插件信息
     * @param plugin 插件信息
     */
    public void savePlugin(Plugin plugin) {
        Plugin current_plugin = getPluginByID(plugin.getId());
        User me = userService.getCurrentDetail();
        if (!current_plugin.getAuthors().contains(me) && !me.getRole().getName().contains("admin")) {
            throw new AccessDenyException();
        }

        current_plugin.setAuthors(plugin.getAuthors());
        current_plugin.getAuthors().add(me);
        current_plugin.setName(plugin.getName());
        current_plugin.setPluginType(plugin.getPluginType());
        current_plugin.setServerType(plugin.getServerType());
        current_plugin.setServerVersion(plugin.getServerVersion());
        current_plugin.setDownload(plugin.getDownload());
        current_plugin.setDepends(plugin.getDepends());
        current_plugin.setPreface(plugin.getPreface());
        current_plugin.setFunctionList(plugin.getFunctionList());
        current_plugin.setUpdateRecord(plugin.getUsageMethod());
        current_plugin.setUpdateRecord(plugin.getUpdateRecord());
        current_plugin.setUpdateTime(new Date());

        pluginRepository.save(current_plugin);
    }

    /**
     * 新建一个插件
     * @param plugin 插件信息
     */
    public void createPlugin(Plugin plugin) {
        User me = userService.getCurrentDetail();
        if (plugin.getAuthors() == null) {
            plugin.setAuthors(Set.of(me));
        }
        plugin.getAuthors().add(me);

        plugin.setUpdateTime(new Date());
        plugin.setPublishTime(new Date());
        pluginRepository.save(plugin);
    }

    /**
     * 分页读取
     * @param page 页码
     * @param size 页面大小
     * @return 插件集合
     */
    public Map<String, Object> getPluginPage(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, getSort());
        Page<Plugin> pages = pluginRepository.findAll(pageable);
        List<Plugin> plugins = pages.getContent();
        for (Plugin plugin : plugins) {
            Set<User> users = plugin.getAuthors();
            for (User user : users) {
                user.setPassword(null);
                user.setIp(null);
            }
        }
        return Map.of("total", pages.getTotalPages(), "page", plugins);
    }

    /**
     * 获取用户发布的插件
     * @param id 用户id
     * @param page 页码
     * @param size 页面大小
     * @return 插件集合
     */
    public Map<String, Object> getMyPlugins(Integer id, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        User user = new User();
        user.setId(id);
        Page<Plugin> pages = pluginRepository.findByAuthor(user.getId(), pageable);
        return Map.of("total", pages.getTotalPages(), "page", pages.getContent());
    }

    /**
     * 获取插件详细信息
     * @param id 插件id
     * @return 详细信息
     */
    public Plugin getPluginInfo(long id) {
        Plugin plugin = getPluginByID(id);
        String info = plugin.getPreface();
        info = plugin.getFunctionList();
        info = plugin.getUsageMethod();
        info = plugin.getUpdateRecord();
        PluginDocument document = plugin.getPluginDocument();
        if (document != null)
            document.setPlugin(null);
        Set<User> users = plugin.getAuthors();
        for (User user : users) {
            user.setPassword(null);
            user.setIp(null);
        }
        return plugin;
    }

    /**
     * 搜索插件
     * @param name 名称
     * @param pluginType 类型
     * @param serverType 服务端类型
     * @param serverVersion 服务端版本
     * @param page 页号
     * @param size 页面大小
     * @return 插件集合
     */
    public Map<String, Object> searchPlugin(String name,
                                     String pluginType,
                                     String serverType,
                                     String serverVersion,
                                     Integer page, Integer size) {
        name = "%" + name + "%";
        pluginType = "%" + pluginType + "%";
        serverType = "%" + serverType + "%";
        serverVersion = "%" + serverVersion + "%";

        Pageable pageable = PageRequest.of(page, size);
        Page<Plugin> pages = pluginRepository.findBySome(name, pluginType, serverType, serverVersion, pageable);
        return Map.of("total", pages.getTotalPages(), "page", pages.getContent());
    }

    /**
     * 模糊搜索
     * @param info 模糊信息
     * @param page 页码
     * @param size 页面大小
     * @return 结果
     */
    public Map<String, Object> searchAll(String info,
                                         Integer page, Integer size) {
        info = "%" + info + "%";
        Pageable pageable = PageRequest.of(page, size);
        Page<Plugin> pages = pluginRepository.findByAll(info, pageable);
        return Map.of("total", pages.getTotalPages(), "page", pages.getContent());
    }

    public Sort getSort() {
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC,"updateTime"));
        orders.add(new Sort.Order(Sort.Direction.DESC,"publishTime"));
        return Sort.by(orders);
    }

    /**
     * 删除插件
     * @param id 插件id
     */
    public void deletePlugin(long id) {
        Plugin plugin = getPluginByID(id);
        PluginDocument document = plugin.getPluginDocument();

        pluginRepository.deletePlugin(id);
        documentRepository.deleteDocumentByPlugin(id);
        copyRepository.deleteCopyByDocument(document.getId());
    }

    /**
     * 获取最热门的插件
     * @return 插件集合
     */
    public List<Plugin> getHotPlugin() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Plugin> pages = pluginRepository.findHot(pageable);
        return pages.getContent();
    }

}
