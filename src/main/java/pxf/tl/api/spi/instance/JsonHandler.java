package pxf.tl.api.spi.instance;

import pxf.tl.api.spi.SpiApi;

import java.util.Map;

/**
 * Json处理器
 *
 * @author potatoxf
 */
public interface JsonHandler extends SpiApi {

    /**
     * 解析Json字符串到Bean中
     *
     * @param json Json字符串
     * @param clz  Bean类
     * @param <T>  Bean类型
     * @return Bean对象
     * @throws Exception 当解析发送异常
     */
    <T> T parseJson(String json, Class<T> clz) throws Exception;

    /**
     * 解析Json字符串到Bean中
     *
     * @param json Json字符串
     * @param bean Bean
     * @param <T>  Bean类型
     * @throws Exception 当解析发送异常
     */
    <T> void parseJson(String json, T bean) throws Exception;

    /**
     * 解析Json字符串到容器中
     *
     * @param json Json字符串
     * @return Map<String, Object>
     * @throws Exception 当解析发送异常
     */
    Map<String, Object> parseJson(String json) throws Exception;

    /**
     * 解析Json字符串到容器中
     *
     * @param json      Json字符串
     * @param container Map<String, Object>
     * @throws Exception 当解析发送异常
     */
    void parseJson(String json, Map<String, Object> container) throws Exception;

    /**
     * 将对象转换成Json
     *
     * @param input 输入对象，Bean或集合
     * @return Json字符串
     */
    String toJson(Object input);
}
