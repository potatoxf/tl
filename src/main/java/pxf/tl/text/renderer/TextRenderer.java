package pxf.tl.text.renderer;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 文本渲染器
 *
 * @author potatoxf
 */
public interface TextRenderer {

    /**
     * 渲染文本成图画
     *
     * @param text   文本
     * @param width  宽度
     * @param height 高度
     * @return {@code BufferedImage}
     */
    BufferedImage render(String text, int width, int height) throws IOException;
}
