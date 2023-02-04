package pxf.tlx.servlet;


import pxf.tl.help.Whether;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 独自用户认证
 *
 * @author potatoxf
 * *
 */
public class AloneHttpBasicAuthentication implements Filter, HttpSessionListener {
    /**
     * 指定值
     */
    private static final Object value = new Object();
    /**
     * 保存是否有用户登录
     */
    private final AtomicBoolean hasUser = new AtomicBoolean(false);
    /**
     * 存贮在会话键
     */
    private final String key;
    /**
     * 用户名
     */
    private final String username;
    /**
     * 密码
     */
    private final String password;
    /**
     * 有效时间
     */
    private int availableTimeSecond = 60 * 60;

    /**
     * @param key
     * @param username
     * @param password
     */
    public AloneHttpBasicAuthentication(String key, String username, String password) {
        this.key = key;
        this.username = username;
        this.password = password;
    }

    /**
     * @return
     */
    public boolean hasUser() {
        return hasUser.get();
    }

    /**
     * @return
     */
    public String getKey() {
        return key;
    }

    /**
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return
     */
    public int getAvailableTimeSecond() {
        return availableTimeSecond;
    }

    /**
     * @param availableTimeSecond
     */
    public void setAvailableTimeSecond(int availableTimeSecond) {
        this.availableTimeSecond = availableTimeSecond;
    }

    /**
     * @param filterConfig
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(
            ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        Object object = session.getAttribute(key);
        if (hasUser.get()) {
            if (object != null) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                response.setStatus(401);
                response.setHeader("WWW-Authenticate", "Basic realm=Only one user is allowed to log in!");
            }
        } else {
            boolean isSuccess = false;
            String basicValue = request.getHeader("Authorization");
            if (Whether.noEmpty(basicValue)) {
                String[] authorizationValue = basicValue.split(" ");
                if (authorizationValue.length == 2) {
                    String base64Encoded = new String(Base64.getDecoder().decode(authorizationValue[1]));
                    if (Whether.noEmpty(base64Encoded)) {
                        String[] userAndPwd = base64Encoded.split(":");
                        if (userAndPwd.length == 2
                                && username.equals(userAndPwd[0])
                                && password.equals(userAndPwd[1])) {
                            if (hasUser.compareAndSet(false, true)) {
                                isSuccess = true;
                                session.setAttribute(key, value);
                                session.setMaxInactiveInterval(availableTimeSecond);
                                filterChain.doFilter(servletRequest, servletResponse);
                            }
                        }
                    }
                }
            }
            if (!isSuccess) {
                response.setStatus(401);
                response.setHeader("WWW-Authenticate", "Basic realm=Verify Http Basic permissions!");
            }
        }
    }

    /**
     *
     */
    @Override
    public void destroy() {
    }

    /**
     * @param se
     */
    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    /**
     * @param se
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        Object attribute = se.getSession().getAttribute(key);
        if (attribute != null) {
            hasUser.compareAndSet(true, false);
        }
    }

    /**
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AloneHttpBasicAuthentication)) return false;

        AloneHttpBasicAuthentication that = (AloneHttpBasicAuthentication) o;

        return key.equals(that.key);
    }

    /**
     * @return
     */
    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
