package crack.cduestc.jw.net.captcha;

import java.awt.image.BufferedImage;

/**
 * 验证码校验器
 */
public interface ICaptcha {
    String ocr(BufferedImage image);
}
