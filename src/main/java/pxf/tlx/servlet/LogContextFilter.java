package pxf.tlx.servlet;

import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * 在日志上下文增加参数，在日志中可以打印该参数，如RequestId，则日志配置：%d{yyyy-MM-dd HH:mm:ss.SSS} [%X{RequestId}] [%thread] %-5level %logger{36} - %msg%n
 * 则 RequestId 会打印在中括号里，%X{参数名}  是格式
 * <p>
 * 该bean自动被注册，默认的mapping是/*，参考文档
 * https://docs.spring.io/spring-boot/docs/2.2.1.RELEASE/reference/htmlsingle/#howto-add-a-servlet-filter-or-listener-as-spring-bean （说明了只要是Filter/Servlet/*Listener的bean自动配置）
 * https://docs.spring.io/spring-boot/docs/2.2.1.RELEASE/reference/htmlsingle/#boot-features-embedded-container-servlets-filters-listeners-beans （说明了Filters默认map to /*）
 * <p>
 * 如果需要复杂点的配置，例如多个Filter的顺序、mapping路径的指定，可以在@Configuration类里使用@Bean来产生所需的bean，产生的过程即可指定顺序和mapping路径
 *
 * @author potatoxf
 */
public class LogContextFilter implements Filter {
    /**
     * 设置响应头，响应头名字（惯例是大写）
     */
    private static final String REQUEST_ID_HEADER = "RequestId";
    /**
     * 设置到 MDC 里的key（惯例使用小驼峰，为了统一这里也用大写）
     */
    private static final String REQUEST_ID = "RequestId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestId = httpRequest.getHeader(REQUEST_ID_HEADER);
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }

        // 还有更加准确的方法
        String ip = ServletHelper.getRequestSourceIp(httpRequest);

        MDC.put(REQUEST_ID, requestId);
        MDC.put("IP", ip);
        httpResponse.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

}

