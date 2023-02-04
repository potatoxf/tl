package pxf.tl.image.captcha;


import pxf.tl.image.TextImage;

import java.io.IOException;

/**
 * 验证码提供器
 *
 * @author potatoxf
 */
public interface CaptchaProducer {

    /**
     * 创建一个验证码
     *
     * @return {@code TextImage}
     * @throws IOException 如果发生异常
     */
    TextImage createCaptcha() throws IOException;
}
