package pxf.tlx.spring.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pxf.tl.date.SystemClock;
import pxf.tl.image.TextImage;
import pxf.tl.image.captcha.CaptchaProducer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 验证码访问接口
 *
 * @author potatoxf
 */
@Api(tags = {"验证码访问接口"})
@RestController
public class CaptchaController {
    private final CaptchaProducer captchaProducer;
    private final String captchaKey;
    private final String captchaTimeKey;

    public CaptchaController(CaptchaProducer captchaProducer, String captchaKey, String captchaTimeKey) {
        this.captchaProducer = captchaProducer;
        this.captchaKey = captchaKey;
        this.captchaTimeKey = captchaTimeKey;
    }

    /**
     * 将验证码写入Session
     *
     * @param request  {@code HttpServletRequest}
     * @param response {@code HttpServletResponse}
     * @throws IOException 如果发送异常
     */
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = void.class)
    })
    @ApiOperation(value = "获取验证码", notes = "获取验证码")
    @RequestMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Set to expire far in the past.
        response.setDateHeader("Expires", 0);
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");
        TextImage textImage = captchaProducer.createCaptcha();

        String format = textImage.format();
        // return a jpeg
        response.setContentType("image/" + format);
        // create the text for the image
        String capText = textImage.getText();

        // store the text in the session
        request.getSession().setAttribute(captchaKey, textImage.getText());

        // store the date in the session so that it can be compared
        // against to make sure someone hasn't taken too long to enter
        // their kaptcha
        request.getSession().setAttribute(captchaTimeKey, SystemClock.now());
        response.getOutputStream().write(textImage.image());
    }
}
