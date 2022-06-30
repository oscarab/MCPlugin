package pers.oscar.mcplugin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pers.oscar.mcplugin.entity.Plugin;
import pers.oscar.mcplugin.entity.PluginDocument;
import pers.oscar.mcplugin.entity.User;
import pers.oscar.mcplugin.exception.PostException;
import pers.oscar.mcplugin.exception.ResourceNotFoundException;
import pers.oscar.mcplugin.repository.DocumentCopyRepository;
import pers.oscar.mcplugin.repository.DocumentRepository;

import java.util.*;

@Service
public class DocumentService {
    private UserService userService;
    private DocumentRepository documentRepository;
    private DocumentCopyRepository copyRepository;

    @Autowired
    public void setCopyRepository(DocumentCopyRepository copyRepository) {
        this.copyRepository = copyRepository;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setDocumentRepository(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    private void checkAuthor(PluginDocument pluginDocument, User me) {
        Plugin plugin = pluginDocument.getPlugin();
        if (me == null) me = userService.getCurrentUser();
        if (!me.getOwnPlugins().contains(plugin)) {
            throw new PostException("无权为该插件创建（修改）文档");
        }
    }

    /**
     * 给一个插件创建文档
     * @param pluginDocument 文档
     */
    public void createDocument(PluginDocument pluginDocument) {
        checkAuthor(pluginDocument, null);
        pluginDocument.setUpdateTime(new Date());
        documentRepository.save(pluginDocument);
    }

    /**
     * 保存文档
     * @param pluginDocument 文档
     */
    public void saveDocument(PluginDocument pluginDocument) {
        PluginDocument current = getDocumentByID(pluginDocument.getId());
        User me = userService.getCurrentUser();
        if (!me.getCoDocuments().contains(pluginDocument) && !me.getRole().getName().contains("admin")) {
            checkAuthor(pluginDocument, me);
        }

        current.setCooperators(pluginDocument.getCooperators());
        current.getCooperators().add(me);
        current.setUpdateTime(new Date());
        current.setApi(pluginDocument.getApi());
        current.setExplanation(pluginDocument.getExplanation());
        current.setFaq(pluginDocument.getFaq());

        documentRepository.save(current);
    }

    /**
     * 获取文档信息
     * @param id 文档id
     * @return 文档
     */
    public PluginDocument getDocumentByID(Long id) {
        Optional<PluginDocument> optional = documentRepository.findById(id);
        if (optional.isEmpty()) {
            throw new ResourceNotFoundException("找不到文档");
        }
        return optional.get();
    }

    /**
     * 获取文档详细信息
     * @param id 文档Id
     * @return 文档
     */
    public PluginDocument getDocumentInfo(Long id) {
        PluginDocument pluginDocument = getDocumentByID(id);
        String info = pluginDocument.getExplanation();
        info = pluginDocument.getFaq();
        info = pluginDocument.getApi();
        Plugin plugin = pluginDocument.getPlugin();
        plugin.setPluginDocument(null);
        Set<User> users = pluginDocument.getCooperators();
        for (User user : users) {
            user.setPassword(null);
            user.setIp(null);
        }
        return pluginDocument;
    }

    /**
     * 获取用户的文档
     * @param id 用户id
     * @param page 页码
     * @param size 页面大小
     * @return 文档集合
     */
    public Map<String, Object> getUserDocument(Integer id, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PluginDocument> pages = documentRepository.findByCooperator(id, pageable);
        List<PluginDocument> documents = documentRepository.findByCooperator(id, pageable).getContent();
        for (PluginDocument document : documents) {
            String name = document.getPlugin().getName();
        }
        return Map.of("total", pages.getTotalPages(), "page", documents);
    }

    /**
     * 删除文档
     * @param id 文档id
     */
    public void deleteDocument(long id) {
        PluginDocument document = getDocumentByID(id);

        documentRepository.deleteDocument(id);
        copyRepository.deleteCopyByDocument(id);
    }
}
