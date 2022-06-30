package pers.oscar.mcplugin.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import pers.oscar.mcplugin.exception.PostException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

@Service
public class UploadService {
    @Value(value = "${resources_path}")
    private String resourcePath;

    /**
     * 保存文件
     * @param multipartFile 原始文件
     * @param root 保存路径
     * @param type 文件类型
     */
    private File saveFile(MultipartFile multipartFile, String root, String type) {
        // 可以使用UUID(唯一通用识别码)来保证文件名的统一
        String uuidFileName = UUID.randomUUID().toString();
        File file = new File(root + "/" + uuidFileName + type);
        try {
            // 文件保存操作
            FileCopyUtils.copy(multipartFile.getInputStream(), Files.newOutputStream(file.toPath()));
        } catch (IOException e) {
            throw new PostException("保存图片失败");
        }
        return file;
    }

    /**
     * 检查文件
     * @param multipartFile 文件
     * @param allowType 运行的类型
     * @return 文件名
     */
    private String checkFile(MultipartFile multipartFile, List<String> allowType) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new PostException("文件为空");
        }
        String file_name = multipartFile.getOriginalFilename();
        assert file_name != null;
        int begin = file_name.lastIndexOf(".");
        int last = file_name.length();
        String type = file_name.substring(begin, last);

        if(!allowType.contains(type))
            throw new PostException("不允许该文件格式");
        return type;
    }

    /**
     * 上传图片
     * @param multipartFile 图片文件
     * @return 文件路径
     */
    public String uploadPicture(MultipartFile multipartFile) {
        String type = checkFile(multipartFile, List.of(".jpg", ".png"));

        File file = saveFile(multipartFile, resourcePath + "/image", type);
        return file.getName();
    }
}
