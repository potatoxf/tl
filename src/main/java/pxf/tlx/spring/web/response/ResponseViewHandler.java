package pxf.tlx.spring.web.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import pxf.tl.help.Whether;
import pxf.tl.util.ToolString;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Set;

/**
 * 响应视图处理器
 *
 * @author potatoxf
 */
public class ResponseViewHandler implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseViewHandler.class);

    private Map<String, Object> commonViewParameter;

    private boolean isPathSplitStart = false;

    private Map<String, String> qualifiedTemplateRootPathMapping;

    private Map<String, String> packageWildcardTemplateRootPathMapping;

    private Set<String> wildcardSet;

    public Map<String, Object> getCommonViewParameter() {
        return commonViewParameter;
    }

    public void setCommonViewParameter(Map<String, Object> commonViewParameter) {
        this.commonViewParameter = commonViewParameter;
    }

    public boolean isPathSplitStart() {
        return isPathSplitStart;
    }

    public void setPathSplitStart(boolean pathSplitStart) {
        isPathSplitStart = pathSplitStart;
    }

    public Map<String, String> getPackageWildcardTemplateRootPathMapping() {
        return packageWildcardTemplateRootPathMapping;
    }

    public void setPackageWildcardTemplateRootPathMapping(Map<String, String> packageWildcardTemplateRootPathMapping) {
        this.packageWildcardTemplateRootPathMapping = packageWildcardTemplateRootPathMapping;
    }

    public Map<String, String> getQualifiedTemplateRootPathMapping() {
        return qualifiedTemplateRootPathMapping;
    }

    public void setQualifiedTemplateRootPathMapping(Map<String, String> qualifiedTemplateRootPathMapping) {
        this.qualifiedTemplateRootPathMapping = qualifiedTemplateRootPathMapping;
    }

    @Override
    public void postHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            View view = modelAndView.getView();
            if (view instanceof RedirectView) {
                return;
            }
            String viewName = modelAndView.getViewName();
            int i = viewName.indexOf(":");
            if (i > 0) {
                String substring = viewName.substring(0, i);
                if ("redirect".equalsIgnoreCase(substring) || "forward".equalsIgnoreCase(substring)) {
                    return;
                }
            }
            String newViewName = null;
            if (handler instanceof HandlerMethod handlerMethod) {
                Class<?> declaringClass = handlerMethod.getBeanType();
                ResponseViewRootPath responseViewRootPath = declaringClass.getAnnotation(ResponseViewRootPath.class);
                if (responseViewRootPath == null) {
                    responseViewRootPath = declaringClass.getPackage().getAnnotation(ResponseViewRootPath.class);
                }
                if (responseViewRootPath != null) {
                    newViewName = ToolString.clearPath(isPathSplitStart, false, responseViewRootPath.value(), viewName);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(String.format("The origin path [%s] and new path [%s] in class [%s] with TemplateRootPath",
                                viewName, newViewName, declaringClass));
                    }
                }
                String name = declaringClass.getName();
                if (newViewName == null && qualifiedTemplateRootPathMapping != null) {
                    newViewName = ToolString.clearPath(isPathSplitStart, false, qualifiedTemplateRootPathMapping.get(name), viewName);
                }
                if (newViewName == null && packageWildcardTemplateRootPathMapping != null) {
                    if (wildcardSet == null) {
                        wildcardSet = packageWildcardTemplateRootPathMapping.keySet();
                    }
                    for (String wildcard : wildcardSet) {
                        if (Whether.matchWildcard(name, wildcard)) {
                            newViewName = ToolString.clearPath(isPathSplitStart, false, packageWildcardTemplateRootPathMapping.get(wildcard), viewName);
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(String.format("The origin path [%s] and new path [%s] in class [%s] with matching [%s]",
                                        viewName, newViewName, declaringClass, wildcard));
                            }
                            break;
                        }
                    }
                }
            }
            if (newViewName == null) {
                newViewName = ToolString.clearPath(isPathSplitStart, false, modelAndView.getViewName());
            }
            if (newViewName != null) {
                modelAndView.setViewName(newViewName);
            }

            if (commonViewParameter != null) {
                modelAndView.addAllObjects(commonViewParameter);
            }
        }
    }

}
