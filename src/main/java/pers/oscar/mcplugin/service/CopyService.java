package pers.oscar.mcplugin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pers.oscar.mcplugin.entity.*;
import pers.oscar.mcplugin.exception.AccessDenyException;
import pers.oscar.mcplugin.exception.PostException;
import pers.oscar.mcplugin.exception.ResourceNotFoundException;
import pers.oscar.mcplugin.repository.DocumentCopyRepository;
import pers.oscar.mcplugin.repository.DocumentRepository;
import pers.oscar.mcplugin.repository.NotifyRepository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CopyService {
    private DocumentCopyRepository documentCopyRepository;
    private UserService userService;
    private DocumentService documentService;
    private DocumentRepository documentRepository;
    private NotifyRepository notifyRepository;

    @Autowired
    public void setNotifyRepository(NotifyRepository notifyRepository) {
        this.notifyRepository = notifyRepository;
    }

    @Autowired
    public void setDocumentRepository(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Autowired
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setDocumentCopyRepository(DocumentCopyRepository documentCopyRepository) {
        this.documentCopyRepository = documentCopyRepository;
    }

    public PluginDocumentCopy getCopy(long id) {
        Optional<PluginDocumentCopy> optional = documentCopyRepository.findById(id);
        if (optional.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        return optional.get();
    }

    /**
     * 创建插件文档副本
     * @param copy 文档副本
     */
    public void createCopy(PluginDocumentCopy copy) {
        User user = userService.getCurrentDetail();
        PluginDocument pluginDocument = copy.getPluginDocument();

        if (documentCopyRepository.existsByAuthorAndPluginDocument(user, pluginDocument)) {
            throw new PostException("你只能拥有文档的一份副本");
        }

        copy.setCreatedTime(new Date());
        copy.setUpdateTime(new Date());
        copy.setAuthor(user);
        copy.setDeleted(false);

        documentCopyRepository.save(copy);
    }

    /**
     * 编辑文档副本
     * @param copy 文档副本
     */
    public void saveCopy(PluginDocumentCopy copy) {
        User user = userService.getCurrentUser();
        if (!user.getCoDocumentsCopy().contains(copy)) {
            throw new AccessDenyException();
        }

        PluginDocumentCopy current_copy = getCopy(copy.getId());
        copy.setUpdateTime(new Date());
        copy.setCreatedTime(current_copy.getCreatedTime());
        copy.setAuthor(userService.getCurrentDetail());

        documentCopyRepository.save(copy);
    }

    /**
     * 获取插件文档详情
     * @param id 文档id
     * @return 插件文档
     */
    public PluginDocumentCopy getCopyInfo(long id) {
        PluginDocumentCopy copy = getCopy(id);
        String info = copy.getApi();
        info = copy.getExplanation();
        info = copy.getFaq();
        PluginDocument pluginDocument = copy.getPluginDocument();

        return copy;
    }

    /**
     * 分页获取用户的文档副本
     * @param id 用户id
     * @param page 页码
     * @param size 每页大小
     * @return 分页
     */
    public Map<String, Object> getUserCopy(Integer id, Integer page, Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdTime");
        Pageable pageable = PageRequest.of(page, size, sort);

        User user = new User();
        user.setId(id);
        Page<PluginDocumentCopy> pages = documentCopyRepository.findByAuthor(user, pageable);
        List<PluginDocumentCopy> copies = pages.getContent();
        for (PluginDocumentCopy copy : copies) {
            Plugin plugin = copy.getPluginDocument().getPlugin();
            String name = plugin.getName();
        }

        return Map.of("total", pages.getTotalPages(), "page", copies);
    }

    /**
     * 合并文档
     * @param notify_id 通知id
     * @param accept 是否同意
     */
    public void mergeDocument(long notify_id, long accept) {
        User user = userService.getCurrentDetail();
        Notify notify = notifyRepository.findById(notify_id).orElse(null);
        if (notify == null || notify.getState() != Notify.State.WAITE) {
            throw new ResourceNotFoundException();
        }
        PluginDocument pluginDocument = notify.getDocument();
        if (!pluginDocument.getCooperators().contains(user)) {
            throw new AccessDenyException();
        }

        if (accept != 0) {
            notify.setState(Notify.State.ACCEPT);
        }
        else {
            notify.setState(Notify.State.REJECT);
        }
        notifyRepository.save(notify);
        if (accept == 0) return;

        PluginDocumentCopy copy = notify.getCopy();
        pluginDocument.setExplanation(copy.getExplanation());
        pluginDocument.setFaq(copy.getFaq());
        pluginDocument.setApi(copy.getApi());
        pluginDocument.setUpdateTime(new Date());

        documentRepository.save(pluginDocument);
    }

    /**
     * 提起合并请求
     * @param doc_id 文档id
     * @param copy_id 副本id
     */
    public void pullRequest(long doc_id, long copy_id) {
        PluginDocumentCopy copy = getCopy(copy_id);
        User user = userService.getCurrentDetail();
        if (copy.getAuthor().getId() != user.getId()) {
            throw new AccessDenyException();
        }
        if (notifyRepository.existsWait(copy_id) > 0) {
            throw new PostException("已存在一个未处理的合并请求");
        }

        PluginDocument pluginDocument = documentService.getDocumentByID(doc_id);
        Notify notify = new Notify();
        notify.setUser(user);
        notify.setCopy(copy);
        notify.setDocument(pluginDocument);
        notify.setPlugin(pluginDocument.getPlugin());
        notify.setState(Notify.State.WAITE);
        notify.setTime(new Date());

        notifyRepository.save(notify);
    }
}
