package pers.oscar.mcplugin.common.captcha;

import lombok.Data;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

@Data
public class ImageCode {
    private String text;
    private BufferedImage image;
    private LocalDateTime expireTime;

}
