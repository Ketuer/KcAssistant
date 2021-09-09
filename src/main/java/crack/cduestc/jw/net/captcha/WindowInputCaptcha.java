package crack.cduestc.jw.net.captcha;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class WindowInputCaptcha implements ICaptcha{

    @Override
    public String ocr(BufferedImage image) {
        return (String) JOptionPane.showInputDialog(null, "请输入验证码：", "登陆到教务系统",
                JOptionPane.QUESTION_MESSAGE, new ImageIcon(image), null, null);
    }
}
